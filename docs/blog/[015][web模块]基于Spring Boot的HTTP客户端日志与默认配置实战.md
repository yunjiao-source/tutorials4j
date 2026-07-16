# [015][web模块]基于Spring Boot的HTTP客户端日志与默认配置实战

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在微服务架构中，服务间的HTTP调用无处不在。一个可靠、可观测的HTTP客户端基础设施，能够极大地提升问题排查效率和开发体验。本文围绕一套完整的Spring Boot HTTP客户端配置方案，深入分析其日志记录、异常处理及默认头注入的实现思路与代码细节。

## 一、背景与目标

在Spring生态中，我们通常使用三种方式发起HTTP请求：

- `RestTemplate`（传统同步阻塞）
- `RestClient`（Spring Boot 3.2+ 新同步客户端）
- `WebClient`（响应式非阻塞）

面对多种客户端，我们希望统一实现：
- **请求/响应日志**：记录请求行、请求头、响应状态码、响应头。
- **异常处理**：对非2xx响应抛出业务异常，携带响应体信息。
- **默认头注入**：自动添加如`X-Request-Id`、`Authorization`等公共头。
- **链路追踪支持**：生成`traceId`/`spanId`，便于日志关联。

本文分析的代码正是为了满足上述需求而设计，支持三种客户端的一致配置。

## 二、整体结构

```
tutorials4j.framework.web
├── rest
│   ├── util
│   │   └── RestUtils.java               // 公共工具 + WebClient Filter 工厂
│   └── interceptor
│       └── LogClientHttpRequestInterceptor.java  // RestTemplate/RestClient 拦截器
├── rest.autoconfigure
│   ├── ClientLoggerConfiguration.java   // 日志自动配置
│   └── ClientDefaultConfiguration.java  // 默认头 + 异常处理自动配置
└── core.properties
    └── WebClientProperties.java         // 配置属性类
```

核心设计思想：**通过自动配置向容器中的客户端构建器（Builder/Customizer）添加行为，从而无侵入地增强所有客户端实例**。

## 三、核心组件详解

### 1. `RestUtils` – 通用工具与WebClient扩展点

`RestUtils` 提供了三个关键能力：

#### ① 链路标识生成

```java
public static String generateTraceId() {
    return IdUtil.fastSimpleUUID();
}

public static String generateSpanId() {
    return IdUtil.fastSimpleUUID().substring(0, 8);
}
```

利用`hutool`工具生成简化UUID，便于在日志中追踪单次请求链路。开发者可在请求头中携带这些ID，并在下游服务中传递。

#### ② 异常捕获Filter（WebClient）

```java
public static ExchangeFilterFunction ofCatchExcepitonLogger() {
    return ExchangeFilterFunction.ofResponseProcessor(response -> {
        if (response.statusCode().value() > 300) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new WebFrameworkException("接口调用异常: " + body)));
        }
        return Mono.just(response);
    });
}
```

**关键点**：
- 对`>=300`的状态码（包括301/302重定向、4xx/5xx）视为异常。
- **消费响应体**作为异常消息，注意这会使得下游无法再次读取body。适合在全局异常处理层使用，避免业务代码重复处理错误状态码。

#### ③ 请求/响应日志Filter（WebClient）

```java
public static ExchangeFilterFunction ofClientRequestLogger() {
    return ExchangeFilterFunction.ofRequestProcessor(request -> {
        requestLoggerDetails(request);
        return Mono.just(request);
    });
}
```

通过`requestLoggerDetails`和`responseLoggerDetails`两个私有辅助方法，将请求方法、URL、所有头部以及响应状态码、所有头部以`INFO`级别输出。输出格式清晰：

```
[ClientRequest]请求: GET https://api.example.com/data
请求头列表: 
Accept=application/json
Authorization=Bearer xxxx
```

**设计亮点**：将请求/响应头日志分离为独立的Filter，便于按需组合（例如只记录请求，不记录响应）。

### 2. `LogClientHttpRequestInterceptor` – 同步客户端拦截器

对于`RestTemplate`和`RestClient`（底层同样基于`ClientHttpRequestInterceptor`），实现了统一的`ClientHttpRequestInterceptor`接口：

```java
@Override
public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpRequestUtils.requestLogger(request, body);   // 记录请求
    ClientHttpResponse response = execution.execute(request, body);
    HttpRequestUtils.responseLogger(response);       // 记录响应
    return response;
}
```

这里将日志记录委托给一个外部的工具类`HttpRequestUtils`（未给出具体实现，但预期会打印请求行、请求头、响应状态码、响应头等信息）。这种委托模式保持了`RestUtils`和`HttpRequestUtils`的职责分离。

### 3. `ClientLoggerConfiguration` – 日志自动配置

该配置类负责向三种客户端**无条件**添加日志记录能力：

```java
@Bean
RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
    return restTemplate -> restTemplate.getInterceptors().add(new LogClientHttpRequestInterceptor());
}

@Bean
RestClientCustomizer logHeadersRestClientCustomizer() {
    return restClientBuilder -> restClientBuilder.requestInterceptor(new LogClientHttpRequestInterceptor());
}

@Bean
WebClientCustomizer logHeadersWebClientCustomizer() {
    return webClientBuilder -> {
        webClientBuilder.filter(RestUtils.ofClientRequestLogger());
        webClientBuilder.filter(RestUtils.ofClientResponseLogger());
    };
}
```

**核心机制**：Spring Boot的`RestTemplateCustomizer`、`RestClientCustomizer`、`WebClientCustomizer`会扫描所有Bean，并自动应用到应用创建的客户端实例上。因此开发者不需要手动为每个`RestTemplate`添加拦截器。

### 4. `ClientDefaultConfiguration` – 默认头与异常处理

此类提供两个核心增强：

#### ① 默认请求头注入

```java
@Bean
RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(WebClientProperties properties) {
    return request -> properties.getDefaultHeaders().forEach(request.getHeaders()::set);
}
```

- `RestTemplateRequestCustomizer`是Spring Boot 2.x之后提供的接口，与`RestTemplateCustomizer`配合，作用于每次请求创建时。
- 同理，为`RestClient`和`WebClient`也通过各自的`Customizer`添加默认头。

`WebClientProperties`绑定配置前缀（如`tutorials4j.web.client.default-headers`），用户可在`application.yml`中配置：

```yaml
tutorials4j:
  web:
    client:
      default-headers:
        X-Source: my-app
        Accept-Language: zh-CN
```

#### ② WebClient全局异常处理

```java
@Bean
WebClientCustomizer defaultWebClientCustomizer() {
    return restClientBuilder -> restClientBuilder.filter(RestUtils.ofCatchExcepitonLogger());
}
```

注意：该方法返回的`WebClientCustomizer`会添加异常捕获Filter，但仅对响应式`WebClient`生效。**为什么不给RestTemplate/RestClient添加类似处理？** 因为它们通常使用`ErrorHandler`机制，或者开发者可以在调用处自行捕获`RestClientException`。当然，你也可以扩展`RestTemplate`的`ResponseErrorHandler`实现类似功能，但本文方案未包含（留给读者自行扩展）。

### 5. `WebClientProperties` – 配置属性

简单的`@ConfigurationProperties`类，用于承载`defaultHeaders`。通过`@Data`简化getter/setter，并提供了默认空`HashMap`避免NPE。

## 四、使用方式

1. **引入依赖**（假设该框架已发布为Starter）。
2. **配置默认请求头**（可选）：
   ```yaml
   tutorials4j.web.client.default-headers:
     X-Trace-Id: ${TRACE_ID:unknown}
   ```
3. **自动生效**：
   - 项目中任意注入`RestTemplate`、`RestClient`、`WebClient`，自动拥有日志和默认头能力。
   - WebClient还自动拥有非2xx转异常的能力。
4. **自定义扩展**：如果你需要使用`traceId`，可以在创建请求时利用`RestUtils.generateTraceId()`设置到请求头中。

## 五、设计优点与注意事项

### 优点

- **零侵入**：通过Customizer机制，无需修改业务代码。
- **客户端一致**：对三种主流客户端提供一致的日志和默认头行为。
- **响应式友好**：WebClient的Filter全程响应式，不阻塞线程。
- **可插拔**：日志Filter和异常Filter独立，可按需启用（本方案中异常Filter单独配置，便于用户排除）。
- **配置驱动**：默认头通过外部配置管理，运维灵活。

### 注意事项

- **响应体只能消费一次**：`RestUtils.ofCatchExcepitonLogger()`中调用了`response.bodyToMono(String.class)`，这会消费掉body，后续如果业务代码还想读取body会失败。因此建议只将此Filter置于最顶层（如全局异常处理），或者除了记录错误消息外不依赖body内容。
- **日志性能**：打印请求头时，注意不要打印敏感信息（如Authorization、Cookie）。可以考虑增加配置开关，或者对特定头值进行脱敏。
- **日志重复**：如果同时使用了`RestTemplate`的日志拦截器和底层`ClientHttpRequestFactory`的日志，可能会产生重复输出。本例中仅靠拦截器输出一次，是合理的。
- **WebClient默认异常**：本例只对状态码>=300抛出异常，但实际业务中可能需要区分对待（如301重定向不一定算异常）。可根据需要修改判断条件。

## 六、扩展思路

### 1. 集成分布式追踪

可以在`RestUtils`中增加一个`ExchangeFilterFunction`，自动从当前上下文（如MDC或Reactor Context）中提取`traceId`，并添加到请求头中。类似地，`LogClientHttpRequestInterceptor`也可以做相同事情。

### 2. 重试与超时配置

当前代码未涉及重试和超时，但可以基于同样的Customizer机制添加`WebClient`的`exchangeStrategies`、`RestTemplate`的`RequestConfig`等。

### 3. 响应体日志（慎用）

如果需要打印响应体，可以将日志Filter修改为`exchangeFilterFunction`包裹，在获取body后打印再重新生成新的`ClientResponse`（需缓冲body）。但注意性能和大响应体可能导致内存问题。

## 七、总结

本文展示了一套生产级的Spring Boot HTTP客户端统一配置方案。通过合理利用Spring Boot提供的Customizer钩子，以及WebClient的Filter机制，实现了日志记录、默认头注入、异常转换等通用能力。这套模式在微服务网关、业务服务调用外部API等场景下非常实用，能大幅提升可观测性和代码复用性。

开发者可以将这些配置封装为一个独立的`spring-boot-starter`，引入后即获得全自动的客户端增强，减少重复劳动，专注核心业务逻辑。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e