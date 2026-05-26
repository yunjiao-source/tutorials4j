# web-distributed-tracing

## 代码功能分析

该项目是一个基于 **Spring Boot 3** 的全链路追踪（Distributed Tracing）示例，核心利用 **MDC**（Mapped Diagnostic Context）实现请求级别的追踪信息传递，并在日志、异步任务、跨服务调用和异常处理中自动携带这些信息。下面分模块说明：

### 1. 追踪标识的定义与生成
- **`TraceConstants`**：定义追踪相关的键名常量
    - `X-Trace-Id`：全局追踪ID
    - `X-Span-Id`：当前跨度ID
    - `X-Parent-Span-Id`：父跨度ID
- **`TraceIdGenerator`**：生成唯一ID的工具
    - `generateTraceId()`：生成无横线的UUID作为全局追踪ID
    - `generateSpanId()`：取UUID前8位作为跨度ID

### 2. 请求入口的追踪初始化（Filter）
- **`TraceFilter`**（`@Order(1)`）
    - 从HTTP请求头中获取 `X-Trace-Id`、`X-Span-Id`、`X-Parent-Span-Id`
    - 若请求头缺失，则自动生成新的 `traceId` 和 `spanId`
    - 将这些值设置到 **MDC** 中，供当前线程的所有日志输出使用
    - 将 `traceId` 和 `spanId` 添加到HTTP响应头（便于客户端或下游服务识别）
    - 请求处理完毕后清除MDC，防止上下文污染

### 3. 日志输出配置
- **`logback-spring.xml`**
    - 日志模式中包含 `%X{X-Trace-Id:-NO_TRACE}`、`%X{X-Span-Id:-NO_SPAN}`、`%X{X-Parent-Span-Id:-NO_PARENT_SPAN}`
    - 每条日志都会自动打印当前请求的追踪信息，未设置时显示默认值

### 4. 异步任务中的MDC传递
- **`DemoAsyncService`**：带有 `@Async` 的异步方法
- **`AsyncConfig`**：配置线程池 `ThreadPoolTaskExecutor`，并设置了自定义的 **`MdcTaskDecorator`**
    - `MdcTaskDecorator` 在提交任务时复制当前线程的MDC上下文
    - 在子线程执行前恢复MDC，执行后清除
    - 保证异步方法中也能正确打印调用方的 `traceId`、`spanId` 等

### 5. 跨服务调用（RestTemplate 与 WebClient）的追踪传递
- **`TraceRestTemplateInterceptor`**（用于 `RestTemplate`）
    - 在发送HTTP请求前，从当前MDC中获取 `traceId` 和 `spanId`
    - 将 `traceId` 添加到请求头
    - 生成新的子 `spanId` 作为当前请求的 `X-Span-Id`，原 `spanId` 作为 `X-Parent-Span-Id`
    - 这样下游服务可继续链路追踪
- **`WebConfig`**
    - 配置 `WebClient` 的 `filter`，逻辑与上述拦截器相同
    - 配置 `RestTemplate` 时添加 `TraceRestTemplateInterceptor`
- **`DemoController`** 中的 `/rest-template` 和 `/web-client` 端点演示了调用百度时自动携带追踪头

### 6. 全局异常处理中的追踪信息
- **`GlobalExceptionHandler`**
    - 使用 `@RestControllerAdvice` 捕获所有异常
    - 从MDC获取当前的 `traceId`
    - 记录异常日志（自动包含追踪信息）
    - 返回 `ErrorResponse` 对象，其中 `traceId` 字段会填入当前追踪ID，方便前端或调用方定位问题

### 7. 业务演示接口（`DemoController`）
- **`/rest-template`**：演示 `RestTemplate` 调用并传递追踪信息
- **`/web-client`**：演示 `WebClient` 调用并传递追踪信息
- **`/async`**：演示异步方法执行时MDC自动传递

### 8. 整体链路流程
1. 客户端请求进入 `TraceFilter`，初始化/提取 `traceId`、`spanId` 并放入MDC。
2. 控制器方法执行，日志自动打印追踪ID。
3. 若调用下游服务（RestTemplate/WebClient），拦截器从MDC获取信息并添加到请求头，同时生成子 `spanId`。
4. 下游服务收到请求后，其 `TraceFilter` 会从请求头提取这些信息，继续传递。
5. 若触发异步任务，`MdcTaskDecorator` 将当前MDC复制到子线程，异步日志同样包含正确追踪ID。
6. 若发生异常，全局异常处理器返回包含 `traceId` 的错误响应。
7. 请求结束，`TraceFilter` 清理MDC。

### 总结
该项目实现了一套**无侵入的全链路追踪方案**，仅通过Filter、拦截器、TaskDecorator和日志配置，即可在单个Spring Boot应用内以及跨服务调用时自动传递追踪ID，极大地方便了问题定位和日志聚合分析。