# [016][web模块]基于 MDC 的分布式追踪框架设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework


## 摘要

在微服务架构中，一个业务请求往往跨越多个服务节点，传统的日志查看方式难以将分散在各处的日志串联起来形成完整的调用链。本文介绍一套基于 SLF4J MDC（Mapped Diagnostic Context）实现的轻量级分布式追踪框架，通过 Servlet 过滤器、HTTP 客户端拦截器、WebFlux 过滤器以及任务装饰器，实现 TraceId 在全链路中的自动传递与日志关联。该框架无需引入重量级 APM 系统，即可快速为 Spring Boot 应用提供基础的全链路追踪能力。

## 1. 背景介绍

分布式追踪是微服务可观测性的重要组成部分。常见的解决方案如 Zipkin、Jaeger、SkyWalking 等，虽然功能强大，但对于中小型项目而言可能存在接入成本高、资源占用大等问题。一种更轻量的替代方案是利用 SLF4J MDC 机制：将 TraceId 存储在线程上下文中，日志模板自动输出该 ID，并通过 HTTP 请求头在服务间传递，从而实现调用链的标识与串联。

本文介绍的框架（代码来源于 `tutorials4j.framework.web.rest.mdc` 包）正是基于上述思想，为 Spring Boot 应用提供了一套开箱即用的全链路追踪能力，同时支持传统的 Servlet 容器和响应式 WebFlux 环境，并能处理异步任务场景下的上下文传递。

## 2. 核心组件解析

### 2.1 TraceFilter（入口过滤器）

`TraceFilter` 实现了 `jakarta.servlet.Filter`，是整个追踪链路的入口（对外接收请求）。

```java
String traceId = httpRequest.getHeader(DefaultConsts.HTTP_TRACE_ID);
if (traceId == null || traceId.isEmpty()) {
    traceId = RestUtils.generateTraceId();
}
MDC.put(DefaultConsts.HTTP_TRACE_ID, traceId);
httpResponse.setHeader(DefaultConsts.HTTP_TRACE_ID, traceId);
```

**核心职责**：
- 从 HTTP 请求头中提取 `traceId`、`spanId`、`parentSpanId`，若缺失则自动生成
- 将这些追踪 ID 存入当前线程的 MDC 上下文
- 将 `traceId` 和 `spanId` 回写到响应头，方便客户端或网关获取
- 请求处理完成后清理 MDC，避免线程池复用导致上下文污染

该过滤器通过配置类 `TraceConfiguration` 注册为 `FilterRegistrationBean`，并设置为最高优先级（`Ordered.HIGHEST_PRECEDENCE`），确保在最早阶段完成追踪 ID 的初始化。

### 2.2 TraceRestTemplateInterceptor（同步客户端拦截器）

用于 Spring 的 `RestTemplate` 同步 HTTP 客户端，在发出请求前自动注入追踪头信息。

```java
String traceId = MDC.get(DefaultConsts.HTTP_TRACE_ID);
if (traceId != null) {
    request.getHeaders().add(DefaultConsts.HTTP_TRACE_ID, traceId);
}
String spanId = MDC.get(DefaultConsts.HTTP_TRACE_SPAN_ID);
if (spanId != null) {
    String childSpanId = RestUtils.generateSpanId();
    request.getHeaders().add(DefaultConsts.HTTP_TRACE_PARENT_SPAN_ID, spanId);
    request.getHeaders().add(DefaultConsts.HTTP_TRACE_SPAN_ID, childSpanId);
}
```

**设计要点**：
- 从当前 MDC 获取父 `spanId`，生成子 `spanId` 并放入请求头
- 同时保留父子关系（`parentSpanId`），供下游服务还原调用树
- 该拦截器同样适用于 `RestClient`（通过 `RestClientCustomizer`）

### 2.3 TraceExchangeFilterFunction（响应式客户端过滤器）

针对 Spring WebFlux 的 `WebClient` 设计的 `ExchangeFilterFunction`，除了添加请求头外，还需处理 Reactor 上下文中的 MDC 传递问题。

```java
Map<String, String> currentMdc = MDC.getCopyOfContextMap();
Map<String, String> toPropagate = currentMdc.entrySet().stream()
    .filter(entry -> DefaultConsts.HTTP_MDC_KEYS.contains(entry.getKey()))
    .collect(...);
ClientRequest filteredRequest = ClientRequest.from(request)
    .headers(headers -> toPropagate.forEach(headers::set))
    .build();
return next.exchange(filteredRequest)
    .contextWrite(Context.of(DefaultConsts.MDC_CONTEXT_KEY, toPropagate))
    .doOnEach(signal -> { /* 从响应头更新 MDC */ });
```

**关键点**：
- 使用 `contextWrite` 将 MDC 快照存入 Reactor 上下文，而非依赖线程局部变量
- 在收到响应时，从响应头提取追踪 ID 并更新当前 MDC（支持下游服务生成新 TraceId 后回传）
- 仅传播配置在 `DefaultConsts.HTTP_MDC_KEYS` 中的特定键值对，避免过度传递

### 2.4 TraceTaskDecorator（异步任务装饰器）

当应用使用 `@Async` 或手动提交 `Runnable` 到线程池时，子线程默认无法继承父线程的 MDC。`TraceTaskDecorator` 通过装饰 `Runnable` 解决了这一痛点。

```java
Map<String, String> contextMap = MDC.getCopyOfContextMap();
return () -> {
    try {
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        runnable.run();
    } finally {
        MDC.clear();
    }
};
```

该装饰器通过 `CompositeTaskDecorator` 机制集成到 Spring 的 `TaskDecorator` 体系中，可与其他装饰器组合使用。

### 2.5 TraceConfiguration（自动配置类）

`@Configuration` 类，条件化注册所有追踪组件：

| Bean | 作用 |
|------|------|
| `FilterRegistrationBean<TraceFilter>` | 注册 Servlet 过滤器，支持通过 `WebHttpProperties.trace` 定制 URL 模式、顺序等 |
| `RestTemplateCustomizer` | 为所有 `RestTemplate` Bean 添加 `TraceRestTemplateInterceptor` |
| `RestClientCustomizer` | 为 `RestClient.Builder` 添加同一拦截器 |
| `WebClientCustomizer` | 为 `WebClient.Builder` 添加 `TraceExchangeFilterFunction` |
| `CompositeTaskDecorator` + `TaskDecoratorSupplier` | 为异步任务提供 MDC 传递能力 |

框架预留了 `ServletFilterOptions` 配置类，用户可通过 `application.yml` 灵活调整过滤器行为，例如仅对特定路径启用追踪。

## 3. 工作流程

### 3.1 请求进入（服务端接收）

1. `TraceFilter` 拦截进入的 HTTP 请求  
2. 从请求头读取 `X-Trace-Id` 等字段，若无则生成  
3. 将追踪 ID 存入 MDC  
4. 将追踪 ID 写入响应头（便于客户端或网关读取）  
5. 调用后续业务逻辑（Controller、Service 等），业务日志自动包含 TraceId

### 3.2 调用下游服务（客户端发送）

- **同步调用（RestTemplate）**：`TraceRestTemplateInterceptor` 从 MDC 取出当前 TraceId 和 SpanId，自动添加到请求头中，并生成子 SpanId  
- **响应式调用（WebClient）**：`TraceExchangeFilterFunction` 从 MDC 取快照，存入 Reactor 上下文并发起请求，响应后可能从响应头更新 MDC

### 3.3 异步任务执行

- 提交任务时，`TraceTaskDecorator` 捕获当前 MDC 快照  
- 子线程执行前恢复 MDC，执行后清理，保证异步日志同样包含正确的 TraceId

### 3.4 完整调用链示例

服务 A（入口）收到请求 → 生成 TraceId=abc，SpanId=1 → 调用服务 B → 请求头携带 `X-Trace-Id: abc, X-Parent-Span-Id: 1, X-Span-Id: 2` → 服务 B 日志输出 `[trace=abc, span=2, parent=1]` → 服务 B 异步处理 → 异步线程日志输出相同的 TraceId。

## 4. 配置与集成

### 4.1 引入依赖

框架假设 `tutorials4j.framework.common.core.DefaultConsts` 中定义了常量键名（如 `HTTP_TRACE_ID`、`HTTP_MDC_KEYS` 等），实际集成时需确保类路径包含这些常量。

### 4.2 配置文件示例（application.yml）

```yaml
tutorials4j:
  web:
    http:
      trace:
        name: customTraceFilter
        url-patterns:
          - "/api/*"
          - "/inner/*"
        order: -100
        dispatcher-types:
          - REQUEST
          - FORWARD
```

### 4.3 日志配置

为了在日志中输出追踪 ID，需要在日志配置文件（如 logback-spring.xml）中引用 MDC 变量：

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [trace=%X{X-Trace-Id},span=%X{X-Span-Id}] %-5level %logger{36} - %msg%n</pattern>
```

## 5. 注意事项

1. **WebFlux 与线程局部 MDC**：Reactor 的线程模型会频繁切换线程，直接依赖 `MDC.put` 可能失效。本框架采用 `contextWrite` 将 MDC 快照存入 Reactor 上下文，并在操作符链中恢复，确保了响应式链路的正确性。但需注意不要在 `subscribe` 之后的异步边界中使用 `MDC.get`，应通过上下文传播。

2. **异步任务覆盖范围**：`TraceTaskDecorator` 仅对通过 Spring `TaskExecutor` 执行的任务生效。如果直接使用 `new Thread()` 或未配置装饰器的线程池，MDC 将无法传递。推荐统一使用 Spring 的 `@Async` 或 `ThreadPoolTaskExecutor` 并设置 `taskDecorator`。

3. **敏感信息过滤**：`DefaultConsts.HTTP_MDC_KEYS` 限制了传播的键名，避免将整个 MDC 内容放入请求头，从而防止内部敏感变量泄露。

4. **性能考虑**：每个请求都会复制 MDC 映射表并遍历过滤，对于高频小请求有一定开销，但通常可接受。若需极致性能，可使用 `ArrayMap` 等轻量结构或在常量层面硬编码传播字段。

5. **SpanId 生成策略**：示例中使用 `RestUtils.generateSpanId()`，实现应保证全局唯一性且尽量简短（如 16 位十六进制）。建议与 TraceId 生成算法保持一致。

## 6. 总结

本文介绍的追踪框架基于 MDC 实现，具有以下优点：

- **轻量无侵入**：仅通过过滤器、拦截器和装饰器完成，不依赖外部存储或代理
- **多环境兼容**：同时支持 Servlet 和 WebFlux，同步与异步客户端全覆盖
- **易于扩展**：通过 `CompositeTaskDecorator` 可组合其他上下文传递逻辑（如安全性、租户 ID）
- **配置灵活**：支持按 URL 模式开关追踪，便于分环境调优

通过该框架，开发者可以在不改造业务代码的前提下，快速获得全链路日志关联能力，显著提升微服务架构下问题定位的效率。对于需要更完整调用链可视化、性能分析等高级功能的场景，建议在此基础上接入 Zipkin 或 Jaeger，本框架的 TraceId 生成与传递机制可以平滑迁移。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e