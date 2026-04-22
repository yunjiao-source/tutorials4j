# common-core


# 基于 Spring Boot CompositeTaskDecorator类实现异步任务上下文多装饰器链

本项目代码在:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-common/common-core

示例代码在:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-examples/examples-common

## 1. 引言

在 Spring Boot 应用中，`@Async` 异步任务和 `TaskExecutor` 线程池是提升系统吞吐量的常用手段。然而，异步任务执行时，线程上下文（如 MDC、请求头、链路追踪信息）往往无法自动从主线程传递到子线程。为此，Spring 提供了 `TaskDecorator` 接口，允许开发者在提交任务前后进行增强操作，例如拷贝上下文、记录日志等。

当业务中需要同时应用多种增强逻辑（如 MDC 传递 + 链路追踪 ID 注入 + 耗时监控）时，手动组合多个 `TaskDecorator` 既繁琐又容易出错。本文介绍了一种优雅的解决方案：通过 `CompositeTaskDecorator` 和 `TaskDecoratorSupplier` 机制，自动收集并组合所有自定义装饰器，实现开箱即用的多装饰器链。

## 2. 背景与痛点

- 单个 `TaskDecorator` 只能执行一种增强逻辑。
- Spring Boot 的 `TaskExecutorAutoConfiguration` 仅支持注入**唯一的** `TaskDecorator` Bean。
- 若定义了多个 `TaskDecorator` Bean，Spring 无法决定使用哪一个，可能导致部分逻辑失效或冲突。
- 手动创建组合装饰器需要硬编码装饰器列表，不利于扩展和解耦。

## 3. 代码解析

### 3.1 `TaskDecoratorSupplier` 接口

```java
@FunctionalInterface
public interface TaskDecoratorSupplier extends Supplier<TaskDecorator> {
    @Override
    TaskDecorator get();
}
```

- 继承自 `Supplier<TaskDecorator>`，是一个函数式接口。
- 作用：**提供 `TaskDecorator` 实例**。每个业务模块可以通过实现该接口或直接提供 `@Bean` 方法，返回自己的 `TaskDecorator` 实现。
- 优势：通过统一的供给者接口，便于框架自动发现和收集。

### 3.2 `CommonCoreConfiguration` 自动配置类

```java
@Configuration(proxyBeanMethods = false)
@Import({SpringUtil.class})
public class CommonCoreConfiguration {
    // ...
    @Bean
    @ConditionalOnMissingBean(TaskDecorator.class)
    CompositeTaskDecorator CompositeTaskDecorator(ObjectProvider<TaskDecoratorSupplier> taskDecoratorSuppliers) {
        List<TaskDecorator> decoratorList = taskDecoratorSuppliers
                .orderedStream()
                .map(TaskDecoratorSupplier::get)
                .collect(Collectors.toList());
        return new CompositeTaskDecorator(decoratorList);
    }
}
```

- **`@ConditionalOnMissingBean(TaskDecorator.class)`**：只有当容器中没有 `TaskDecorator` 类型的 Bean 时，才会创建 `CompositeTaskDecorator`。这保证了用户可以完全自定义一个装饰器（而非组合）来覆盖默认行为。
- **`ObjectProvider<TaskDecoratorSupplier>`**：注入所有 `TaskDecoratorSupplier` 类型的 Bean，并通过 `orderedStream()` 保证顺序（支持 `@Order` 或 `Ordered` 接口）。
- **`CompositeTaskDecorator`**：Spring 内置的装饰器组合器，会按顺序依次调用每个 `TaskDecorator` 的 `decorate` 方法。

## 4. 工作原理

1. **用户定义多个装饰器**  
   每个装饰器以 `TaskDecoratorSupplier` Bean 的形式提供，例如：

   ```java
   @Bean
   public TaskDecoratorSupplier mdcTaskDecorator() {
       return () -> runnable -> {
           Map<String, String> contextMap = MDC.getCopyOfContextMap();
           return () -> {
               MDC.setContextMap(contextMap);
               runnable.run();
               MDC.clear();
           };
       };
   }
   ```

2. **自动配置生效**  
   由于容器中尚无 `TaskDecorator` Bean，`CommonCoreConfiguration` 会创建一个 `CompositeTaskDecorator`，内部收集所有 `TaskDecoratorSupplier` 提供的装饰器。

3. **注入到线程池**  
   Spring Boot 的 `TaskExecutorAutoConfiguration` 会自动检测到唯一的 `TaskDecorator` Bean（即 `CompositeTaskDecorator`），并将其应用到默认的 `ThreadPoolTaskExecutor` 中。

4. **任务执行**  
   当异步任务提交时，`CompositeTaskDecorator` 会按顺序调用每个装饰器的 `decorate` 方法，形成一个包装链，最终执行原始任务。

## 5. 使用示例

假设需要同时支持 MDC 上下文传递和耗时日志打印：

```java
@Configuration
public class MyTaskDecorators {

    @Bean
    @Order(1)
    public TaskDecoratorSupplier mdcDecorator() {
        return () -> runnable -> {
            Map<String, String> ctx = MDC.getCopyOfContextMap();
            return () -> {
                if (ctx != null) MDC.setContextMap(ctx);
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }

    @Bean
    @Order(2)
    public TaskDecoratorSupplier timingDecorator() {
        return () -> runnable -> () -> {
            long start = System.currentTimeMillis();
            runnable.run();
            log.info("Task executed in {} ms", System.currentTimeMillis() - start);
        };
    }
}
```

- `@Order` 控制装饰器执行顺序：先传递 MDC，再记录耗时。
- 无需额外配置，`CompositeTaskDecorator` 会自动组合这两个装饰器。

## 6. 应用场景

| 场景               | 装饰器功能描述                                 |
| ------------------ | ---------------------------------------------- |
| 链路追踪           | 传递 traceId、spanId 到子线程                  |
| MDC 日志上下文     | 复制日志关联 ID（如 userId、requestId）        |
| 安全上下文         | 传递 `SecurityContext` 或认证信息              |
| 性能监控           | 记录任务执行时间、成功率                       |
| 异常处理           | 统一捕获并记录异步任务中的异常                 |
| 数据库连接上下文   | 传递 Hibernate Session 或事务上下文            |

## 7. 注意事项

- **Bean 名称冲突**：如果用户自行定义了一个 `TaskDecorator` Bean（非 `CompositeTaskDecorator`），则自动配置的 `CompositeTaskDecorator` 不会生效，此时需要用户手动处理多装饰器组合。
- **顺序控制**：使用 `@Order` 或实现 `Ordered` 接口来控制装饰器的执行顺序，`orderedStream()` 会按升序排列。
- **性能开销**：装饰器链会为每个任务增加若干层包装，应避免在装饰器中执行过重的操作。
- **异常传播**：装饰器内部的异常会包装在 `RejectedExecutionException` 或任务执行异常中，需谨慎处理。
- **线程安全**：`TaskDecorator` 的 `decorate` 方法会被多线程并发调用，实现时应当是无状态的或线程安全的。

## 8. 总结

本文介绍的 `TaskDecoratorSupplier` + `CompositeTaskDecorator` 组合模式，提供了一种声明式、可扩展的多装饰器管理方案。开发者只需实现 `TaskDecoratorSupplier` 接口并暴露为 Spring Bean，框架便会自动收集并按顺序组合成一个全局装饰器，无缝集成到 Spring Boot 的异步任务线程池中。

这种设计遵循了开闭原则（对扩展开放，对修改关闭），避免了硬编码装饰器列表，提升了代码的可维护性和可读性。在实际项目中，非常适合用于统一管理异步任务的通用增强逻辑，如链路追踪、日志上下文传递等。

---

**参考代码**：
- `CompositeTaskDecorator` 位于 `org.springframework.core.task.support` 包。
- Spring Boot 自动配置原理参见 `TaskExecutionAutoConfiguration`。


# Spring Boot 条件注解扩展：基于 Map/List 配置的精细化控制

本项目代码在:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-common/common-core

示例代码在:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-examples/examples-common


在 Spring Boot 应用中，`@ConditionalOnProperty` 是一个常用的条件注解，它允许根据配置属性的存在与否或具体值来决定 Bean 的加载。然而，当配置属性是 **Map** 或 **List** 结构时，原生的条件注解无法直接判断集合是否为空。例如，我们可能需要根据 `my.servers` 这个 Map 中是否有配置项来决定是否启用某个客户端，或者根据 `app.features` 这个 List 是否为空来决定是否加载某些功能。

为了解决这一痛点，本文介绍一组自定义的 Spring Boot 条件注解 —— **`@ConditionalOnMapProperty`** 和 **`@ConditionalOnListProperty`**，它们能够灵活地判断配置中的 Map 或 List 是否为空，并支持缺失配置时的匹配策略。

## 一、注解概述

这组注解位于 `tutorials4j.framework.common.core.condition` 包下，包含两个核心注解：

- `@ConditionalOnMapProperty`：判断指定的配置键对应的值是否为 **空 Map**（`isEmpty() == true`）。
- `@ConditionalOnListProperty`：判断指定的配置键对应的值是否为 **空 List**。

两者均支持以下特性：
- 通过 `prefix` + `name`（或直接使用 `value`）灵活指定配置键。
- 通过 `isEmpty` 参数控制匹配条件：`true` 表示“Map/List 为空时匹配”，`false` 表示“Map/List 非空时匹配”。
- 通过 `matchIfMissing` 参数控制当配置键不存在时的匹配行为。

## 二、注解属性详解

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `prefix` | String | `""` | 配置前缀，例如 `"my.map"`。 |
| `name` | String | `""` | 属性名称，与 `prefix` 拼接形成完整配置键。 |
| `value` | String | `""` | `name` 的别名，两者互斥，推荐只使用其中一个。 |
| `isEmpty` | boolean | `false` | `true`：要求 Map/List 为空时条件匹配；`false`：要求 Map/List 非空时匹配。 |
| `matchIfMissing` | boolean | `false` | `true`：配置键缺失时条件匹配；`false`：缺失时不匹配。 |

> 注：完整配置键的构建规则为：
> - 若 `prefix` 为空，则直接使用 `name`（或 `value`）。
> - 若 `name` 为空，则使用 `prefix`。
> - 否则使用 `prefix + "." + name`。

## 三、使用示例

假设我们在 `application.yml` 中有以下配置：

```yaml
my:
  map:
    servers:
      usa: "8.8.8.8"
      europe: "1.1.1.1"
  list:
    features: ["cache", "logging"]
  empty-map: {}
  missing-key: ~   # 不存在
```

### 示例 1：当 Map 非空时加载 Bean

```java
@Bean
@ConditionalOnMapProperty(prefix = "my.map", name = "servers", isEmpty = false)
public DataCenterClient dataCenterClient() {
    return new DataCenterClient();
}
```
由于 `my.map.servers` 是一个包含两个条目的 Map，非空，因此 `isEmpty = false` 匹配，Bean 会被创建。

### 示例 2：当 List 为空时加载配置类

```java
@Configuration
@ConditionalOnListProperty(prefix = "my.list", name = "empty-features", isEmpty = true, matchIfMissing = true)
public class FallbackConfig {
    // ...
}
```
假设 `my.list.empty-features` 不存在，由于 `matchIfMissing = true`，条件匹配，配置类生效。

### 示例 3：直接使用 `value` 指定完整键

```java
@Bean
@ConditionalOnMapProperty(value = "my.empty-map", isEmpty = true)
public EmptyMapHandler emptyMapHandler() {
    return new EmptyMapHandler();
}
```
`my.empty-map` 存在且为空 Map，`isEmpty = true` 匹配，Bean 被加载。

## 四、工作原理

该实现基于 Spring Boot 的 `SpringBootCondition` 抽象类，并利用了 `Binder` API 将配置属性绑定为 `Map` 或 `List` 类型。整体流程如下：

1. **注解元数据解析**：`AbstractOnCollectionCollecitonCondition`（抽象基类）在 `getMatchOutcome` 中读取注解属性（`prefix`、`name`、`value`、`isEmpty`、`matchIfMissing`），并构建完整的配置键 `fullKey`。
2. **委托给子类**：子类 `OnCollectionMapCondition` 或 `OnCollectionListCondition` 实现 `makeDecision` 方法，分别调用 `Binder.get(environment).bind(fullKey, Bindable.mapOf(...))` 或 `Bindable.listOf(...)`。
3. **绑定结果判断**：
   - 若 `bindResult.isBound()` 为 `false`（配置缺失），则条件匹配结果等于 `matchIfMissing`。
   - 若绑定成功，则获取实际集合，判断其是否为 `null` 或空，然后比较 `isEmpty` 与集合的实际空状态是否一致。
4. **输出条件结果**：返回 `ConditionOutcome`，包含匹配与否及详细日志信息。

> 注意：抽象类名中存在笔误 `Colleciton`，实际应为 `Collection`，但不影响功能。

## 五、注意事项

1. **配置值的类型兼容性**  
   绑定时，`OnCollectionMapCondition` 会将配置值绑定为 `Map<String, Object>`，`OnCollectionListCondition` 绑定为 `List<Object>`。如果配置的实际结构不匹配（例如将一个普通字符串写在 `fullKey` 下），`Binder` 可能无法绑定成功，此时 `isBound()` 为 `false`，将被视为“配置缺失”，由 `matchIfMissing` 决定结果。建议确保配置结构与注解意图一致。

2. **嵌套集合的限制**  
   当前实现仅判断 **顶层集合是否为空**，不会递归检查内部元素的空状态。例如，一个 Map 中包含空 List 作为 value，该 Map 整体仍被视为非空。

3. **与 `@ConditionalOnProperty` 的区别**  
   `@ConditionalOnProperty` 关注的是单个属性的存在性、具体值，而本组注解关注集合类型的**整体空状态**，适合控制依赖于“是否有任何配置项”的功能开关。

4. **性能考量**  
   每次条件评估都会触发 `Binder` 绑定操作，对于频繁调用的场景（如许多 Bean 依赖同一配置键），可以考虑将条件结果缓存。不过 Spring Boot 的条件评估本身已有缓存机制，通常无需额外优化。

5. **注解名称及文档**  
   在 `@ConditionalOnListProperty` 的 Javadoc 中，`isEmpty` 的描述误写为“是否要求 Map 为空”，实际应为“是否要求 List 为空”，使用时注意语义。

## 六、总结

`@ConditionalOnMapProperty` 和 `@ConditionalOnListProperty` 为 Spring Boot 的条件化配置提供了更细粒度的集合空判断能力。通过简单的注解属性，开发者可以优雅地根据 Map/List 是否为空来动态装配 Bean，避免编写冗余的 `@Conditional` 实现类。这对于配置驱动的多环境应用、插件化架构或功能开关等场景尤为实用。

该实现代码简洁、依赖 Spring Boot 标准 API（`Binder`），易于集成到现有项目中。如果你需要更复杂的集合判断（如大小阈值、包含特定元素等），可以在其基础上进一步扩展。

---
