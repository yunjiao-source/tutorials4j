这段代码展示了一个基于 **Spring Boot 3** + **WebFlux** + **声明式 HTTP 客户端（`@HttpExchange`）** 的完整示例。它同时演示了如何配置和使用 `RestClient` 与 `WebClient` 两种底层实现来调用外部 REST API（这里使用 JSONPlaceholder 作为示例），并通过自定义过滤器（`ExchangeFilterFunction`）实现横切关注点（日志、重试、认证、添加默认头）。下面按模块逐一分析其功能。

---

## 1. 核心组件概览

| 类/接口 | 职责 |
|--------|------|
| `User` | 用户数据记录（ID、姓名、用户名、邮箱） |
| `UserQuery` | 查询参数记录（用户名、邮箱） |
| `UserApiClient` | 声明式 HTTP 客户端接口，定义对 `/users` 资源的 CRUD 操作 |
| `UserQueryArgumentResolver` | 自定义参数解析器，将 `UserQuery` 对象转换为请求查询参数 |
| `HttpClientConfig` | 配置 `RestClient` / `WebClient` 以及对应的 `UserApiClient` 代理 Bean |
| `DefaultHeaderExchangeFilterFunction` | 为每个请求添加时间戳头 `X-REQUEST-TIMESTAMP` |
| `LoggingExchangeFilterFunction` | 记录请求方法/URL、响应状态码及耗时 |
| `RetryExchangeFilterFunction` | 请求失败时自动重试（最多3次，指数退避） |
| `SecurityExchangeFilterFunction` | 从 Reactor 上下文提取 SecurityContext 并添加 Bearer Token |
| `UserController` | 对外暴露 `/api/users` REST 接口，内部委托给 `UserApiClient` |
| `PageController` | 返回视图名称 `httpexchange`（用于展示测试页面） |

---

## 2. 声明式 HTTP 客户端（`UserApiClient`）

```java
@HttpExchange(url = "/users", accept = "application/json")
public interface UserApiClient {
    @GetExchange
    Flux<User> getAllUsers(UserQuery query);
    @GetExchange("/{id}")
    Mono<User> getById(@PathVariable("id") Long id);
    @PostExchange("/")
    Mono<User> save(@RequestBody User user);
    @PutExchange("/{id}")
    Mono<User> update(@PathVariable("id") Long id, @RequestBody User user);
    @DeleteExchange("/{id}")
    Mono<Void> delete(@PathVariable("id") Long id);
}
```

- **`@HttpExchange`** 定义基础 URL 路径 `/users` 和接受的 Content-Type。
- 方法级别的注解（`@GetExchange`, `@PostExchange` 等）明确 HTTP 方法、子路径。
- `getAllUsers` 的参数是 `UserQuery` 对象，它**不是** Spring 标准参数类型，所以需要自定义 `HttpServiceArgumentResolver`（即 `UserQueryArgumentResolver`）将其字段展开为查询参数。
- 返回类型使用 `Flux` / `Mono`，表明这是一个响应式客户端（Reactive）。

---

## 3. 自定义参数解析器（`UserQueryArgumentResolver`）

```java
public class UserQueryArgumentResolver implements HttpServiceArgumentResolver {
    @Override
    public boolean resolve(Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (parameter.getParameterType().equals(UserQuery.class)) {
            UserQuery search = (UserQuery) argument;
            if (StringUtils.isNotBlank(search.username())) {
                requestValues.addRequestParameter("username", search.username());
            }
            if (StringUtils.isNotBlank(search.email())) {
                requestValues.addRequestParameter("email", search.email());
            }
            return true;
        }
        return false;
    }
}
```

- 当方法参数为 `UserQuery` 类型时，将其非空字段添加为 URL 查询参数。
- 例如：`UserQuery(username="Bret")` 会生成 `?username=Bret`。
- 该解析器在 `HttpClientConfig` 中通过 `customArgumentResolver()` 注册到 `HttpServiceProxyFactory`。

---

## 4. 客户端配置（`HttpClientConfig`）

### 4.1 RestClient 版本

```java
@Bean
public RestClient restClient() {
    return RestClient.builder().baseUrl("https://jsonplaceholder.typicode.com").build();
}

@Bean
public UserApiClient userApiRestClient(RestClient restClient) {
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
            .customArgumentResolver(new UserQueryArgumentResolver())
            .build();
    return factory.createClient(UserApiClient.class);
}
```

- 创建 `RestClient` 实例，设置基础 URL。
- 使用 `RestClientAdapter` 将其适配到 `HttpServiceProxyFactory`，从而生成 `UserApiClient` 的代理对象。
- **同步/阻塞**风格（但可以配合响应式编程使用）。

### 4.2 WebClient 版本（被 `UserController` 实际使用）

```java
@Bean
public WebClient webClient() {
    return WebClient.builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .filter(new DefaultHeaderExchangeFilterFunction())
            .filter(new RetryExchangeFilterFunction())
            .filter(new LoggingExchangeFilterFunction())
            .filter(new SecurityExchangeFilterFunction())
            .build();
}

@Bean
public UserApiClient userApiWebClient(WebClient webClient) {
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(webClient))
            .customArgumentResolver(new UserQueryArgumentResolver())
            .build();
    return factory.createClient(UserApiClient.class);
}
```

- 构建 `WebClient` 时链式添加了四个过滤器（执行顺序为添加顺序）。
- `UserController` 中通过 `@Qualifier("userApiWebClient")` 注入了 WebClient 版本的代理。

---

## 5. 过滤器（ExchangeFilterFunction）详解

### 5.1 `DefaultHeaderExchangeFilterFunction`
```java
ClientRequest modified = ClientRequest.from(clientRequest)
    .header("X-REQUEST-TIMESTAMP", LocalDateTime.now().toString())
    .build();
```
为每个外出请求添加当前时间戳头部，可用于服务端调试或追踪。

### 5.2 `LoggingExchangeFilterFunction`
- 请求前：打印 `🌐 Request: METHOD URL`
- 响应后（或异常）：计算耗时（毫秒），打印状态码或错误信息。
- 使用 `doOnNext` 和 `doOnError` 实现无侵入式日志。

### 5.3 `RetryExchangeFilterFunction`
```java
RetryBackoffSpec spec = Retry.backoff(3, Duration.ofSeconds(1))
    .maxBackoff(Duration.ofSeconds(5))
    .doAfterRetry(rs -> log.info("重试 {}", rs.totalRetries()));
return nextFilter.exchange(clientRequest).retryWhen(spec);
```
- 当请求失败（如网络异常、5xx 响应）时自动重试，最多 3 次。
- 退避策略：初始延迟 1 秒，最大延迟 5 秒。
- 每次重试时记录日志。

### 5.4 `SecurityExchangeFilterFunction`
- 从 `ReactiveSecurityContextHolder` 获取当前 `SecurityContext`。
- 提取 `Authentication` 的 `credentials`（假设是字符串 Token）。
- 如果认证有效且 Token 非空，则添加 `Authorization: Bearer <token>` 头。
- 若无法获取 Token 或 SecurityContext 为空，则记录警告并继续原请求（不添加认证头）。
- **注意**：此过滤器要求上游（如 Spring Security）已经将认证信息放入 Reactor 上下文。

---

## 6. `UserController` – 对外 REST 接口

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserApiClient userApiClient; // 注入的是 userApiWebClient

    @GetMapping
    public Flux<User> getAllUsers(UserQuery query) { ... }
    // 其他 CRUD 方法...
}
```

- 将客户端请求直接委托给 `UserApiClient` 的对应方法。
- `getAllUsers` 方法接收 `UserQuery` 对象（Spring MVC 会自动将查询参数绑定到该 record 字段），然后传递给底层声明式客户端。
- 返回的 `Flux`/`Mono` 由 WebFlux 直接写回响应（非阻塞）。

---

## 7. 整体工作流程

1. **客户端（浏览器或 REST 工具）** 调用 `GET /api/users?username=Bret`。
2. `UserController.getAllUsers` 被触发，参数 `query` 的 `username="Bret"`。
3. `userApiClient.getAllUsers(query)` 执行：
    - `UserQueryArgumentResolver` 将 `query` 转换为查询参数 `?username=Bret`。
    - `HttpServiceProxyFactory` 生成的代理通过 `WebClient` 发送请求 `https://jsonplaceholder.typicode.com/users?username=Bret`。
4. 请求经过 `WebClient` 的过滤器链（顺序执行）：
    - `DefaultHeaderExchangeFilterFunction`：添加 `X-REQUEST-TIMESTAMP`。
    - `RetryExchangeFilterFunction`：包裹重试逻辑。
    - `LoggingExchangeFilterFunction`：记录请求开始。
    - `SecurityExchangeFilterFunction`：尝试添加 Bearer Token（如果上下文中有）。
5. 实际网络请求发出，收到响应后：
    - `LoggingExchangeFilterFunction` 记录响应状态和耗时。
    - 如果失败，`RetryExchangeFilterFunction` 决定是否重试。
6. 响应数据（JSON）被反序列化为 `User` 对象（Flux 可能包含多个）。
7. `UserController` 将结果直接返回给客户端。

---

## 8. 其他细节

- **`PageController`**：处理 `/httpexchange` 请求，返回视图名 `httpexchange`，大概是一个用于测试该功能的 HTML 页面（未提供模板文件）。
- **依赖注意**：`SecurityExchangeFilterFunction` 依赖于 Spring Security 的 `ReactiveSecurityContextHolder`，因此项目中必须引入 `spring-boot-starter-security` 并配置好认证机制（例如 JWT），否则该过滤器总会打印“SecurityContext 为空”。
- **重试风险**：`RetryExchangeFilterFunction` 会对所有异常进行重试，包括 4xx 客户端错误。实际生产环境可能需要更精细的过滤（如只重试 5xx 或网络异常）。
- **`UserQueryArgumentResolver`** 使用了 Apache Commons Lang3 的 `StringUtils`，需要引入对应依赖。

---

## 9. 总结

这段代码完整展示了 Spring Boot 3 中如何利用 **声明式 HTTP 客户端（`@HttpExchange`）** 简化外部 API 调用，同时通过 **`ExchangeFilterFunction`** 以响应式、非阻塞的方式实现横切关注点。它还对比了 `RestClient` 和 `WebClient` 两种底层适配方式，并演示了自定义参数解析器的用法。最终通过 `UserController` 暴露这些能力，形成一套从**用户请求 → 内部声明式客户端 → 外部 API** 的完整链式调用。