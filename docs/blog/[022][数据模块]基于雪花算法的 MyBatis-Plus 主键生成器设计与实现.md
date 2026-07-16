# [022][数据模块]基于雪花算法的 MyBatis-Plus 主键生成器设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j

## 1. 背景

在分布式系统中，数据库主键 ID 的生成是一个常见挑战。传统自增主键在分库分表场景下会失效，而 UUID 虽然全局唯一但占用空间大且无序，影响索引性能。雪花算法（Snowflake）由 Twitter 开源，生成 64 位 Long 型 ID，既能保证全局唯一性，又能保持时间有序，成为分布式 ID 的主流方案。

本文介绍一个在实际项目中整合了 **Hutool 雪花算法** 与 **MyBatis-Plus** 的完整解决方案，包含三个核心组件：

- `SnowflakeUtils`：雪花算法工具类，提供单例封装和系统属性配置。
- `DefaultIdentifierGenerator`：实现 MyBatis-Plus 的 `IdentifierGenerator` 接口，将雪花 ID 接入 MyBatis-Plus。
- `DataMybatisPlusConfiguration`：自动配置类，注册拦截器（分页、乐观锁、防全表更新）并设置全局 ID 生成器。

## 2. 雪花算法工具类：SnowflakeUtils

### 2.1 设计目标

- 提供静态方法 `nextId()` 和 `nextIdStr()`，方便业务代码调用。
- 支持通过 JVM 启动参数动态配置 `workerId` 和 `datacenterId`，适应多节点部署。
- 单例模式避免重复初始化，保证性能。

### 2.2 核心代码解析

```java
public class SnowflakeUtils {
    public static final String WORKER_ID = "TUTORIALS4J_SNOWFLAKE_WORKER_ID";
    public static final String DATACENTER_ID = "TUTORIALS4J_SNOWFLAKE_DATACENTER_ID";

    private Snowflake snowflake;
    private static SnowflakeUtils INSTANCE;

    private SnowflakeUtils() {
        initSnowflake();
    }

    private synchronized void initSnowflake() {
        if (snowflake != null) return;
        long datacenterId = getLongProperty(DATACENTER_ID, 1);
        long workerId = getLongProperty(WORKER_ID, 1);
        snowflake = IdUtil.getSnowflake(workerId, datacenterId);
    }

    // 双重检查锁单例
    protected static SnowflakeUtils getInstance() { ... }

    public static long nextId() {
        return getInstance().snowflake.nextId();
    }

    public static String nextIdStr() {
        return getInstance().snowflake.nextIdStr();
    }
}
```

- **系统属性读取**：例如 `-DTUTORIALS4J_SNOWFLAKE_WORKER_ID=2`，若未配置则默认 `1`。
- **异常处理**：当属性值非数字时抛出 `FrameworkRuntimeException`，防止静默失败。
- **Hutool 集成**：直接复用 `IdUtil.getSnowflake()`，减少重复造轮子。

### 2.3 使用示例

```java
// 生成 long 型 ID
long id = SnowflakeUtils.nextId();

// 生成字符串型 ID（内部仍是雪花算法，非 UUID）
String idStr = SnowflakeUtils.nextIdStr();
```

## 3. MyBatis-Plus 标识生成器适配：DefaultIdentifierGenerator

MyBatis-Plus 允许通过 `IdentifierGenerator` 接口自定义主键生成策略。我们的实现非常简单：

```java
public class DefaultIdentifierGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return SnowflakeUtils.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        return SnowflakeUtils.nextIdStr();
    }
}
```

- `nextId()` 用于 `@TableId(type = IdType.ASSIGN_ID)` 且字段类型为数值（Long/Integer）的场景。
- `nextUUID()` 用于字段类型为字符串的场景，但注意这里生成的是雪花 ID 的字符串形式，并非真正的 UUID，名称上可优化。

## 4. MyBatis-Plus 自动配置：DataMybatisPlusConfiguration

该配置类的作用是在 Spring Boot 环境中自动装配 MyBatis-Plus 的核心能力。


### 4.1 全局 ID 生成器配置

```java
@Bean
public MybatisPlusPropertiesCustomizer defaultIdentifierGeneratorMybatisPlusPropertiesCustomizer() {
    return plusProperties -> plusProperties.getGlobalConfig()
            .setIdentifierGenerator(new DefaultIdentifierGenerator());
}
```

这样所有使用 `IdType.ASSIGN_ID` 的实体都会自动使用雪花算法生成主键，无需在每个实体类上重复配置。

## 5. 整体工作流程

1. **Spring Boot 启动** → 加载 `DataMybatisPlusConfiguration`。
2. **配置全局 ID 生成器** → 将 `DefaultIdentifierGenerator` 设置到 MyBatis-Plus 全局配置中。
3. **业务插入数据时** → MyBatis-Plus 检测到主键为 `ASSIGN_ID` 策略 → 调用 `DefaultIdentifierGenerator.nextId()` → `SnowflakeUtils.nextId()` → 生成全局唯一有序 ID。

## 6. 部署配置说明

在分布式部署时，需要为每个节点分配唯一的 `workerId` 和 `datacenterId`。推荐通过环境变量或 JVM 参数注入：

```bash
java -DTUTORIALS4J_SNOWFLAKE_WORKER_ID=1 \
     -DTUTORIALS4J_SNOWFLAKE_DATACENTER_ID=1 \
     -jar your-app.jar
```

- `workerId` 范围：0~31（Hutool 默认支持 5 bit，可自定义扩展）
- `datacenterId` 范围：0~31
- 确保所有节点组合唯一，否则可能生成重复 ID。

## 7. 优缺点分析

### 7.1 优点

- **全局唯一且有序**：雪花 ID 基于时间戳，天然适合作为数据库主键，利于 B+ 树索引。
- **高性能**：本地生成，无网络开销，单机每秒可达百万级。
- **配置灵活**：通过系统属性控制 worker 和 datacenter，适合容器化部署。

### 7.2 注意事项

- **时钟回拨问题**：Hutool 的 `Snowflake` 默认会抛出异常，生产环境需确保 NTP 服务配置正确或使用可容忍回拨的变种。
- **workerId 分配**：在 Kubernetes 等动态 IP 环境中，需借助外部分发（如 ZooKeeper、Redis）自动分配 workerId。
- **字符串 ID 命名**：`nextUUID` 方法实际生成雪花 ID 的字符串形式，容易引起误解，可重命名为 `nextIdStr`。

## 8. 扩展与改进建议

1. **支持时钟回拨容忍**：可参考百度 UidGenerator 或美团 Leaf，增加缓冲机制。
2. **自动注册 workerId**：基于 Redis 或 etcd 实现 workerId 动态分配与心跳续期。
3. **细化配置**：允许通过 `application.yml` 配置 workerId，而非仅系统属性。

## 9. 总结

本文提供的三个类构成了一套轻量级、可投入生产的分布式主键解决方案。通过合理的工具封装和框架集成，开发者只需引入依赖并配置 JVM 参数，即可享受到雪花算法带来的高性能与全局有序性，同时还能利用 MyBatis-Plus 的拦截器增强数据库操作安全性。

该方案已在多个微服务项目中稳定运行，适用于中大规模分布式系统的主键生成场景。

阅读最新文章，请关注我的微信公众号`杨运交` 