# [079][消息模块]基于 Redis Stream 的高可靠消息队列实现——Spring Data Redis 实战解析

本文章代码: https://gitee.com/yunjiao-source/tutorials4j

## 一、引言

在现代微服务架构中，消息队列是解耦、削峰、异步处理的核心中间件。Redis 自 5.0 版本引入的 **Stream** 数据类型，凭借其持久化、消费者组、ACK 确认等特性，正逐渐成为轻量级消息队列的优选方案。

本文基于一个真实的 Spring Boot 项目代码，深入剖析如何利用 **Spring Data Redis** 封装 Redis Stream，构建一个具备 **消费者组、异常处理、自动清理、多线程消费** 的生产级消息队列组件。我们将从架构设计、核心源码解析到最佳实践，完整展示其实现思路与优化空间。

---

## 二、整体架构与设计理念

项目结构遵循分层与工厂模式，核心类图如下：

```
┌─────────────────────────────────────────────────────────────┐
│                    StreamMessageHandlerFactory             │
│  - 单例工厂，根据 queueKey 创建/获取 Handler               │
│  - 从配置文件读取 StreamQueueOptions 构建 Config            │
└───────────────────────────┬─────────────────────────────────┘
                            │ 1. 创建
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    StreamMessageHandler                     │
│  - 发送消息（send）                                         │
│  - 消费循环（consumer）                                    │
│  - 清理过期消息（trimByMinId）                             │
│  - 优雅关闭（shutdown）                                    │
└───────────────────────────┬─────────────────────────────────┘
                            │ 2. 依赖
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              StreamMessageConsumer (接口)                   │
│  - handleMessage(message)                                  │
│  - handleMessageWhenError(message, throwable)              │
└───────────────────────────┬─────────────────────────────────┘
                            │ 3. 实现
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    EmailConsumer (示例)                     │
│  - 业务处理 + 随机异常模拟                                 │
│  - 实现 Runnable，支持多线程启动                           │
└─────────────────────────────────────────────────────────────┘
```

**设计亮点：**

- **工厂模式**：通过 `StreamMessageHandlerFactory` 统一管理 Handler 实例，避免重复创建，支持动态配置。
- **配置与逻辑分离**：`StreamMessageConfig` 为不可变 Record，`validate()` 确保参数合法性。
- **生命周期管理**：通过 `@PreDestroy` 和 `shutdown()` 实现优雅停机，避免消费线程阻塞 JVM 退出。
- **多消费线程**：每个消费者实例可启动多个线程，实现并发消费。

---

## 三、核心组件详解

### 1. `StreamMessageConfig`——配置中心

```java
@Builder
public record StreamMessageConfig(
    String queueName, 
    int countPreRead, 
    Duration sleepTimeWhenException, 
    Duration blockTimeout,
    String consumerGroup, 
    Duration retentionTime) {
  public void validate() { /* 校验非空、正数等 */ }
}
```

- **queueName**：Stream 的 key。
- **consumerGroup**：消费者组名（用于共享消费进度）。
- **countPreRead**：每次 `XREADGROUP` 拉取的最大条数，控制批量大小。
- **blockTimeout**：阻塞等待新消息的超时时间，避免空轮询。
- **sleepTimeWhenException**：消费异常时的休眠间隔，防止错误日志刷屏。
- **retentionTime**：消息保留时长，配合 `XTRIM` 清理旧数据。


### 2. `StreamMessageHandler`——核心处理器

#### （1）发送消息

```java
public String send(BaseRedisMessage message) {
    // 校验 queueName 匹配
    RecordId recordId = stringRedisTemplate.opsForStream()
        .add(StreamRecords.objectBacked(message).withStreamKey(config.queueName()));
    return recordId != null ? recordId.getValue() : null;
}
```

- 使用 `ObjectRecord` 自动序列化消息体（默认 JdkSerialization 或 Jackson2JsonRedisSerializer）。
- 返回 `RecordId` 作为消息唯一 ID，可用于后续追踪。

#### （2）消费循环——核心逻辑

```java
public void consumer(String consumerName, StreamMessageConsumer consumer) {
    // 1. 尝试创建消费者组（若已存在则忽略异常）
    stringRedisTemplate.opsForStream().createGroup(streamKey, consumerGroup);
    
    while (running.get()) {
        // 2. 阻塞读取新消息（从 lastConsumed 位置）
        List<ObjectRecord<String, BaseRedisMessage>> messages = 
            stringRedisTemplate.opsForStream().read(
                BaseRedisMessage.class,
                Consumer.from(consumerGroup, consumerName),
                StreamReadOptions.empty().block(config.blockTimeout()).count(config.countPreRead()),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed())
            );
        
        // 3. 遍历处理并 ACK
        for (ObjectRecord<String, BaseRedisMessage> record : messages) {
            try {
                consumer.handleMessage(body);
            } catch (Exception e) {
                consumer.handleMessageWhenError(body, e);
            } finally {
                // 无论成功失败均 ACK，表示消息已被消费（避免 Pending 堆积）
                stringRedisTemplate.opsForStream().acknowledge(consumerGroup, record);
            }
        }
    }
}
```

**关键设计决策：**

- **自动创建消费者组**：首次启动时若组不存在则创建，简化运维。
- **阻塞读取**：`block(config.blockTimeout())` 避免 CPU 空转，超时后返回空列表继续循环。
- **先处理再 ACK**：确保消息业务逻辑执行后才确认，但若处理异常则仍 ACK，并将异常交由 `handleMessageWhenError` 处理（如记录日志或转存 DB）。这样设计可防止消息卡死在 Pending 队列，但需注意业务异常时消息会丢失（若未持久化）。实际生产可结合重试或死信队列。
- **异常捕获**：外层 catch 捕获所有异常，防止循环中断，并休眠一段时间再继续。

**潜在风险**：`finally` 中 ACK 发生在 `handleMessageWhenError` 之后，若 `handleMessageWhenError` 抛异常，则 ACK 无法执行，导致消息一直处于 Pending，后续可能被其他消费者重复消费。建议将 ACK 放在 `finally` 块中，但务必保证 `handleMessageWhenError` 不抛异常，或将其与 ACK 顺序调换。

#### （3）消息清理

```java
public long trimByMinId() {
    long threshold = System.currentTimeMillis() - config.retentionTime().toMillis();
    String minId = threshold + "-0";  // 时间戳-序号
    // 执行 XTRIM MINID ~ 近似裁剪
    return stringRedisTemplate.execute((connection) -> 
        (Long)connection.execute("XTRIM", config.queueName().getBytes(),
            "MINID".getBytes(), "~".getBytes(), minId.getBytes())
    );
}
```

- 使用 `MINID` 策略删除 ID 小于 `threshold-0` 的消息（`~` 表示近似裁剪，性能更好）。
- 定时调用（如 `@Scheduled`）可控制 Stream 长度，避免内存膨胀。

#### （4）优雅关闭

```java
public void shutdown() {
    running.set(false);
}
```

- 退出 while 循环，线程自然结束，需配合 `Thread.interrupt()` 处理阻塞中的读取。

### 3. `StreamMessageHandlerFactory`——工厂实现

```java
public class StreamMessageHandlerFactory {
    public static final StreamMessageHandlerFactory instance = new StreamMessageHandlerFactory();
    @Setter private StringRedisTemplate stringRedisTemplate;
    @Setter private Map<String, StreamQueueOptions> streamQueueOptionsMap;
    private final Map<String, StreamMessageHandler> handlerMap = new ConcurrentHashMap<>();

    public StreamMessageHandler handler(String key) {
        return handlerMap.computeIfAbsent(key, this::initHandler);
    }
}
```

- 单例模式，通过 Spring 注入 `StringRedisTemplate` 和配置 Map。
- 懒加载 + `ConcurrentHashMap` 保证线程安全。
- 配置源 `StreamQueueOptions` 可来自 `application.yml`，支持多队列动态扩展。

---

## 四、消息发送与消费流程（以 Email 为例）

### 1. 发送消息

```java
@RestController
public class EmailController {
    @GetMapping("send")
    public String send() {
        IntStream.range(0, 5).forEach(i -> emailService.send());
        return "ok";
    }
}
```

```java
public void send() {
    EmailData data = new EmailData(id.incrementAndGet(), Instant.now());
    emailHandler.send(jacksonRecord.toJson(data));
}
```

- 数据转 JSON，存入 Stream，`queueName` 为 `MESSAGE_KEY_EMAIL` 对应的值。

### 2. 启动消费者

```java
@Component
public class ConsumerInitComponent implements CommandLineRunner {
    private final EmailService emailService;
    @Override
    public void run(String... args) {
        emailService.init();  // 启动两个消费线程
    }
}
```

```java
public void init() {
    emailConsumer.start();  // 每个 start 创建一个新线程
    emailConsumer.start();
}
```

- 两个线程共享同一个 `EmailConsumer` 实例（但每个线程有独立的消费者名称 `instant-${id}`）。
- 两个消费者同属一个消费者组，Redis 会按负载均衡分配消息。

### 3. 消费逻辑

```java
@Override
public void handleMessage(BaseRedisMessage message) {
    SmsData data = jacksonRecord.toObject(message.getData(), SmsData.class);
    if (随机异常) throw new RuntimeException("业务处理异常");
    // 模拟业务耗时
    Thread.sleep(随机毫秒);
    log.info("处理完成：{}", data);
}

@Override
public void handleMessageWhenError(BaseRedisMessage message, Throwable throwable) {
    log.error("处理异常，将消息存入数据库[{}]", message.getId());
}
```

- 模拟 30% 概率异常，异常时仅记录日志，不重试。
- 实际业务可在此处实现重试或失败队列。

### 4. 定时清理

```java
@Scheduled(initialDelay = 3000, fixedDelay = 5000)
public void clean() {
    long count = emailHandler.trimByMinId();
    if (count > 0) log.info("清理了{}条记录", count);
}
```

- 每隔 5 秒清理超过 `retentionTime` 的消息。

---

## 五、高可用与可靠性设计分析

| 特性 | 实现方式 | 评价 |
|------|----------|------|
| **消息持久化** | Redis Stream 默认持久化（RDB/AOF） | 可靠，但非强一致 |
| **消费者组** | `XGROUP` 管理位移，支持多消费者负载均衡 | 成熟，自动故障恢复（若消费者下线，消息可被其他消费者接手） |
| **消息确认** | 手动 ACK，处理成功后才确认 | 确保至少消费一次 |
| **异常处理** | 捕获异常并记录，仍 ACK 避免阻塞 | 丢失异常消息，需额外补偿机制 |
| **重试机制** | 未内置，需自行扩展（如将失败消息发送至延迟队列） | 可增强 |
| **消息清理** | 基于时间的 XTRIM | 有效控制内存，但近似裁剪可能留下少量旧消息 |
| **优雅停机** | `shutdown()` 标志，需处理 `block` 阻塞 | 可优化为 `Thread.interrupt()` + 捕获 InterruptedException |

**改进建议：**

1. **失败重试**：在 `handleMessageWhenError` 中根据重试次数将消息重新发送回 Stream 或存入“重试队列”。
2. **死信队列**：重试次数超限后转入死信主题，人工介入。
3. **监控与告警**：记录 Pending 数量、消费延迟等指标，接入 Prometheus。
4. **序列化优化**：当前使用 JSON，建议统一配置 `Jackson2JsonRedisSerializer` 以提升可读性。

---

## 六、性能优化与最佳实践

### 1. 批量读取与并发消费
- `countPreRead` 设置合理值（如 10~100），减少网络往返。
- 多消费者实例/线程并行，利用 Redis Stream 的消费者组特性实现水平扩展。

### 2. 阻塞超时设置
- `blockTimeout` 不宜过长（如 5~30 秒），避免 shutdown 时长时间阻塞。
- 结合 `Thread.interrupt()` 可快速响应关闭信号。

### 3. 消息体设计
- 使用 `BaseRedisMessage` 统一封装，包含 `id`、`queueName`、`data`、`timestamp` 等字段，便于追踪。
- 业务数据序列化为 JSON，保持跨语言兼容性。

### 4. 清理策略
- 推荐使用 `XTRIM MINID ~` 而非 `MAXLEN`，因为前者与时间关联，更符合业务过期需求。
- 清理频率根据生产速率调整，避免主线程阻塞。

### 5. 工厂配置动态化
- 将 `StreamQueueOptions` 接入配置中心（如 Apollo、Nacos），支持运行时调整参数。

---

## 七、潜在问题与解决方案

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 消息丢失（异常后 ACK） | 业务异常时仍 ACK 且未持久化 | 改为 NACK 或转入重试队列 |
| 重复消费 | 处理完成但 ACK 前消费者崩溃 | 业务幂等设计 |
| 消费线程永不退出 | 阻塞读取未响应 `running` 变化 | 在 `block()` 超时后检查 `running`，或使用 `interrupt()` |
| 消费者组创建异常被忽略 | `createGroup` 失败未区分已存在或其他错误 | 捕获 `RedisSystemException` 并检查错误码，仅忽略“组已存在” |
| 长期空闲的连接超时 | Redis 连接池超时 | 配置合理的超时和心跳 |

---

## 八、总结与展望

本文通过对一套完整 Redis Stream 消息组件的源码剖析，展示了如何利用 Spring Data Redis 构建生产可用的消息队列系统。其设计涵盖了配置管理、消息发送、消费循环、异常处理、定时清理和优雅停机等关键环节，适用于非强一致性、但要求低延迟和高吞吐的场景（如通知推送、日志处理、数据同步等）。

未来可扩展的方向包括：
- 引入 **Spring Cloud Stream** 屏蔽底层细节。
- 集成 **Micrometer** 实现指标监控。
- 支持 **事务性发送** 和 **批量确认**。
- 基于 **Redis 7.0** 的新特性（如消费者组偏移量持久化）进一步优化。

Redis Stream 虽非专用 MQ，但其轻量、易用、与 Redis 生态无缝融合的优势，使其在中小型项目中极具竞争力。希望本文能为您在实际项目中的选型与实现提供有价值的参考。
