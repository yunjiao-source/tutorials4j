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