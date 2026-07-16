#  [007][租户模块]基于 TransmittableThreadLocal 与 TaskDecorator 的租户上下文传递设计

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在 SaaS（Software as a Service）架构中，租户隔离是核心需求之一。每个请求都需要携带租户标识，并在整个处理链路中——包括 Web 层、Service 层以及异步任务——正确传递该标识，确保数据操作和业务逻辑严格限定在当前租户范围内。

本文结合一套自定义的 Spring Boot 扩展框架，详细分析其如何优雅地实现**租户上下文在多线程环境下的传递**。核心思路是：  
- 使用 `TransmittableThreadLocal`（阿里 TTL）存储租户信息，解决普通 `ThreadLocal` 在线程池复用场景下的丢失问题。  
- 通过 `TaskDecorator` + `CompositeTaskDecorator` 机制，自动为所有异步任务装饰上下文传递逻辑。  
- 结合 Web 拦截器（`HandlerInterceptor`）在请求入口设置租户，并在请求结束清理。

## 一、租户上下文持有者：TenantContextHolder

```java
public class TenantContextHolder {
    private static final ThreadLocal<String> CURRENT_CONTEXT = new TransmittableThreadLocal<>();

    public static String get() { /* 返回租户代码，默认大写，缺省为 DEFAULT_TENTANT_CODE */ }
    public static void set(final String tenant) { CURRENT_CONTEXT.set(tenant); }
    public static void clear() { CURRENT_CONTEXT.remove(); }
}
```

关键点：
- 使用 `com.alibaba.ttl.TransmittableThreadLocal` 而非原生 `ThreadLocal`，确保当任务提交到线程池时，父线程的租户上下文能“传递”给子线程。  
- `get()` 方法提供默认租户代码，避免空指针；统一转为大写，保证租户标识规范化。  
- `set` / `clear` 方法成对出现，防止内存泄漏。

## 二、Web 层租户注入：TenantHandlerInterceptor

```java
public class TenantHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, ...) {
        String tenant = ServletUtils.getTenant(request); // 从 Header 或参数中获取
        if (StringUtils.hasText(tenant)) {
            TenantContextHolder.set(tenant);
        }
        return true;
    }

    @Override
    public void afterCompletion(...) {
        TenantContextHolder.clear(); // 请求结束必须清除
    }
}
```

在 `TenantCoreConfiguration` 中注册该拦截器，并配置需要拦截的路径模式（通过 `TenantProperties` 注入）。

## 三、异步任务租户传递的核心：TaskDecorator

Spring 的 `TaskExecutor` 支持配置 `TaskDecorator`，用于装饰提交的 `Runnable` 或 `Callable`。框架设计了一套**自动收集并组合**装饰器的机制。

### 3.1 租户装饰器实现

```java
public class TenantTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        String tenant = TenantContextHolder.get(); // 捕获当前线程的租户
        return () -> {
            try {
                TenantContextHolder.set(tenant);  // 子线程设置
                runnable.run();
            } finally {
                TenantContextHolder.clear();       // 子线程清理
            }
        };
    }
}
```

### 3.2 装饰器供应商：TaskDecoratorSupplier

```java
@FunctionalInterface
public interface TaskDecoratorSupplier extends Supplier<TaskDecorator> { }
```

任何需要提供装饰器的模块，只需实现该接口并声明为 Spring Bean。框架会**自动收集所有该类型的 Bean**，并按顺序组合。

### 3.3 组合装饰器创建器：CompositeTaskDecoratorCreator

```java
public class CompositeTaskDecoratorCreator implements Supplier<CompositeTaskDecorator> {
    private final List<TaskDecoratorSupplier> taskDecoratorSuppliers;

    @Override
    public CompositeTaskDecorator get() {
        // 双重检查锁，单例创建 CompositeTaskDecorator
        List<TaskDecorator> decorators = taskDecoratorSuppliers.stream()
                .map(TaskDecoratorSupplier::get)
                .collect(Collectors.toList());
        // 注意：这里反转了顺序！原因见下文。
        instance = new CompositeTaskDecorator(decorators);
        return instance;
    }
}
```

**为什么需要反转顺序？**  
`CompositeTaskDecorator` 按列表**正向顺序**执行装饰：第一个装饰器包裹最外层，最后一个装饰器最靠近核心业务逻辑。通常我们希望优先级高（Order 值小）的装饰器更靠近核心代码。  
`ObjectProvider.orderedStream()` 返回的 Supplier 列表已是按 `@Order` 或 `Ordered` 升序排列（优先级高的在前）。经过 `reverse` 后，高优先级装饰器位于列表末尾，从而在执行时被更内层调用。  
但在当前代码中，`CompositeTaskDecoratorCreator` **并未对列表进行反转**，仅保留了原始顺序。这与类注释中的设计意图不一致。正确的实现应当先反转，或者由使用者确保 Supplier 的声明顺序已符合需求。**这是一个潜在的改进点**。

### 3.4 自动装配与使用

- `CommonCoreConfiguration` 创建 `CompositeTaskDecoratorCreator` Bean，收集所有 `TaskDecoratorSupplier`。  
- `TenantCoreConfiguration` 中声明 `TaskDecoratorSupplier` 返回 `TenantTaskDecorator`。  
- 同时，`TenantCoreConfiguration` 利用 `CompositeTaskDecoratorCreator` 创建 `CompositeTaskDecorator` 并暴露为 Bean。  
- 最后，业务方在配置 `ThreadPoolTaskExecutor` 时，直接将 `CompositeTaskDecorator` 设置给 `setTaskDecorator()`，即可让所有异步任务自动租户传递。

## 四、架构流程图解

```text
HTTP Request
    │
    ▼
TenantHandlerInterceptor.preHandle()
    │ 设置 TenantContextHolder (主线程)
    ▼
Controller / Service
    │ 可能调用 @Async 方法
    ▼
TaskExecutor.submit(Runnable)
    │ 应用 CompositeTaskDecorator
    │   ├── Decorator1 (如日志MDC)
    │   ├── Decorator2 (TenantTaskDecorator)
    │   └── ...
    ▼
子线程执行 Runnable
    │ TenantTaskDecorator 已将租户上下文传递
    │ 业务代码中 TenantContextHolder.get() 能获取正确租户
    ▼
Runnable 执行完毕 → finally 清理
```

## 五、技术亮点与注意事项

### 5.1 为什么不用 InheritableThreadLocal？
原生 `InheritableThreadLocal` 只能在创建子线程时复制父线程的值，但线程池复用线程时**不会重新传递**，导致租户错乱。`TransmittableThreadLocal` 配合 TTL 的 `TtlRunnable`/`TtlExecutor` 可以解决，但 Spring 的 `TaskDecorator` 机制提供了更轻量的方式——在装饰器中手动复制并设置。

### 5.2 组合装饰器顺序控制
实现类可以通过 `@Order` 或 `Ordered` 接口指定顺序。例如日志 MDC 装饰器通常优先级高于租户装饰器，因为日志输出需要租户信息。框架会按 `orderedStream()` 收集，但 `CompositeTaskDecoratorCreator` 目前**未反转**，使用者需要注意自己的装饰器逻辑是否依赖顺序。建议修正为反转，或在文档中明确说明。

### 5.3 租户默认值与缺省处理
`TenantContextHolder.get()` 返回默认租户 `DEFAULT_TENANT_CODE`，防止未设置租户时执行空逻辑，但也可能掩盖配置错误。可根据业务需要调整为抛出异常。

### 5.4 拦截器路径配置
通过 `TenantProperties.getPathPatterns()` 动态配置，例如 `/api/**`，避免对静态资源或开放接口进行租户拦截。

## 六、总结

本文介绍的租户传递方案具有以下优点：
1. **非侵入**：业务代码只需通过 `TenantContextHolder.get()` 获取租户，无需手动传递参数。
2. **自动覆盖**：Web 请求和异步任务均自动注入租户，开发者无感知。
3. **可扩展**：基于 `TaskDecoratorSupplier` 的 SPI 机制，其他模块可以轻松添加自己的装饰器（如请求追踪 ID、日志 MDC 等）。
4. **线程安全**：利用 TTL 和装饰器确保线程池场景下上下文的正确传递。

实际生产环境中，还需考虑定时任务、MQ 消费、`@Scheduled` 等线程模型，它们同样可以通过类似的 `TaskDecorator` 或自定义拦截器统一处理。本文的架构为多租户 SaaS 应用提供了一个可靠、干净的实现范式。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e