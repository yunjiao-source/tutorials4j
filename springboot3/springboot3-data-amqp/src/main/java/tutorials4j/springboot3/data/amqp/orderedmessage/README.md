## 代码功能分析

该代码实现了一个基于 **Spring Boot + RabbitMQ** 的**有序消息处理系统**，用于保证同一订单的状态变更消息（CREATE → PAY → DELIVER）能够按顺序被消费和处理。

### 1. 核心业务场景

模拟订单生命周期中的三个核心事件：
- **CREATE**：订单创建
- **PAY**：订单支付
- **DELIVER**：订单发货

要求：对于同一个 `orderId`，消息处理的顺序必须严格遵循 `CREATE → PAY → DELIVER`，不允许乱序（例如先收到 PAY 再收到 CREATE）。

### 2. 整体架构设计

- **消息发送端**：`OrderMessageSender`
- **消息队列**：3 个独立队列（`order_2_queue_1`, `order_2_queue_2`, `order_2_queue_3`），绑定到同一个 Topic 交换机（`order_2_exchange`）
- **消息消费端**：`OrderMessageConsumer`，为每个队列配置了一个 `@RabbitListener` 方法
- **状态存储**：`OrderRepository` 使用 `ConcurrentHashMap` 内存存储每个订单的当前状态（INIT / CREATE / PAY / DELIVER）

### 3. 消息发送逻辑（OrderMessageSender）

```java
int queueIndex = Math.abs(orderId.hashCode()) % QUEUE_COUNT;   // 0,1,2
String queueName = ORDER_QUEUE_PREFIX + (queueIndex + 1);
rabbitTemplate.convertAndSend(ORDER_EXCHANGE, queueName, message);
```

- **关键点**：基于订单 ID 的哈希值取模，**保证同一个订单的所有消息永远进入同一个队列**。
- 效果：同一订单的消息在队列内天然保持 FIFO 顺序（前提是生产者按顺序发送）。

### 4. 消息消费与顺序保障（OrderMessageConsumer）

#### 4.1 单队列顺序性
- 每个队列只有一个 `@RabbitListener` 方法消费（单线程顺序拉取消息），因此**队列内部消息自然有序**。

#### 4.2 消息顺序校验
即使消息按顺序到达队列，消费者仍然进行**二次校验**：

```java
String lastType = orderLastMessageType.computeIfAbsent(orderId, orderRepository::get);
if (!checkMessageOrder(lastType, currentType)) {
    channel.basicReject(deliveryTag, true);  // 拒绝并重新入队
    return;
}
```

校验规则：
- 上一条为 `INIT` → 只允许 `CREATE`
- 上一条为 `CREATE` → 只允许 `PAY`
- 上一条为 `PAY` → 只允许 `DELIVER`
- 上一条为 `DELIVER` → 拒绝任何后续消息

若顺序错误，消息被**重新入队**，等待正确的上一个消息被处理后再消费。

#### 4.3 业务处理与重试机制
- 业务成功：更新内存中的订单状态，`basicAck` 确认消费。
- 业务失败：支持最多重试 3 次（通过 `retry-count` 消息头记录），超过后拒绝入队并记录（需人工介入）。

### 5. 模拟数据生成（DemoRunner）

- 使用 `@Scheduled(fixedDelay = 5000)` 每 5 秒执行一次。
- 每次生成 0~9 共 10 条消息，每条消息间隔随机（0~499ms）。
- 通过 `Set<Long>` 记录已发送的消息类型阶段，**随机**为某个 `orderId` 发送 `CREATE` / `PAY` / `DELIVER`（不保证顺序，模拟真实乱序场景）。
- 发送后打印当前所有订单的状态分布（`orderRepository.countOrder()`）。

### 6. 配置亮点（RabbitConfig）

- **队列与交换机**：创建 3 个持久化队列，并通过 `with(queueName)` 绑定，路由键与队列名相同。
- **消息转换器**：使用 `Jackson2JsonMessageConverter` 并显式添加信任包 `tutorials4j.springboot3.data.amqp.orderedmessage`，避免反序列化安全异常。
- **消息持久化**：发送时设置 `MessageDeliveryMode.PERSISTENT`，防止 RabbitMQ 重启丢失消息。

### 7. 局限性说明

- **状态存储**：内存 `ConcurrentHashMap`，应用重启后数据丢失（演示用）。生产环境应改用 Redis 或数据库。
- **性能**：每个订单的消息严格串行，吞吐量受限于单个队列的消费能力，但可通过增加队列数量（`QUEUE_COUNT`）水平扩展不同订单的处理并发。
- **死信处理**：重试 3 次失败后仅打印日志，未接入死信队列或告警。

### 8. 总结

该代码提供了一个**完整且可运行的有序消息消费示例**，核心思想：
> 将需要保序的实体（订单）通过哈希取模固定路由到同一个队列，利用队列的 FIFO 特性和消费者的顺序拉取，结合业务层的状态校验，最终实现全局顺序消费。

适用于对同一主键（如订单ID、用户ID）有严格顺序要求的异步处理场景。