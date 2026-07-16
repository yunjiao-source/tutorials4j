# [025][Web模块]基于 Spring Boot 的请求日志过滤器设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j

## 1. 引言

在 Web 应用开发中，记录 HTTP 请求的详细信息对于问题排查、安全审计和性能分析至关重要。Spring Boot 内置的 `CommonsRequestLoggingFilter` 提供了基础的请求日志能力，但其功能较为有限（例如无法灵活添加时间戳、无法动态控制日志级别等）。本文介绍一种扩展实现——通过自定义配置属性、过滤器及自动配置，构建一个可配置、易集成的高灵活请求日志组件。

## 2. 整体设计

该组件包含三个核心类：

- **WebHttpProperties**：集中管理 Web 层 HTTP 相关配置，其中嵌套了 `RequestLoggingOptions` 用于定制请求日志行为。
- **DefaultCommonsRequestLoggingFilter**：继承 `CommonsRequestLoggingFilter`，重写关键方法以支持时间戳追加和强制日志记录。
- **WebMvcRequestLoggingConfiguration**：Spring Boot 自动配置类，根据属性创建并注册过滤器 Bean。

整体架构图如下：

```
┌─────────────────────────────────────────────────────────┐
│                   application.yml                        │
│  tutorials4j.web.http.request-logging.*                  │
└───────────────────┬─────────────────────────────────────┘
                    │ @ConfigurationProperties
                    ▼
┌─────────────────────────────────────────────────────────┐
│                  WebHttpProperties                       │
│  ┌─────────────────────────────────────────────────┐    │
│  │ +RequestLoggingOptions                           │    │
│  │   - includeTimestamp                             │    │
│  │   - includeClientInfo / Headers / Payload        │    │
│  │   - maxPayloadLength, message prefixes...        │    │
│  └─────────────────────────────────────────────────┘    │
└───────────────────┬─────────────────────────────────────┘
                    │ 注入
                    ▼
┌─────────────────────────────────────────────────────────┐
│          WebMvcRequestLoggingConfiguration               │
│  @Bean FilterRegistrationBean<DefaultCommons...>        │
└───────────────────┬─────────────────────────────────────┘
                    │ 创建并初始化
                    ▼
┌─────────────────────────────────────────────────────────┐
│         DefaultCommonsRequestLoggingFilter              │
│  - 重写 createMessage() 追加 timestamp                  │
│  - 重写 shouldLog() 始终返回 true                       │
│  - init() 将 options 映射到父类属性                     │
└─────────────────────────────────────────────────────────┘
```

## 3. 核心组件详解

### 3.1 配置属性类 `WebHttpProperties`

该类使用 `@ConfigurationProperties(prefix = "tutorials4j.web.http")`，将外部配置绑定到结构化对象。其中 `RequestLoggingOptions` 内部类定义了以下关键配置项：

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `filter` | `ServletFilterOptions` | name="requestLoggingServletFilter" | 通用的过滤器注册参数（启用状态、URL 模式、顺序等） |
| `includeTimestamp` | `boolean` | false | 是否在日志消息末尾追加当前毫秒时间戳 |
| `includeClientInfo` | `boolean` | false | 是否记录客户端地址、Session ID、用户信息 |
| `includeHeaders` | `boolean` | false | 是否记录请求头 |
| `includePayload` | `boolean` | false | 是否记录请求体内容 |
| `maxPayloadLength` | `int` | 300 | 记录请求体的最大字符数 |
| `beforeMessagePrefix` / `suffix` | `String` | "请求前 [" / "]" | 请求处理前日志的前后缀 |
| `afterMessagePrefix` / `suffix` | `String` | "请求后 [" / "]" | 请求处理后日志的前后缀 |

> **设计亮点**：通过嵌套 `ServletFilterOptions`，复用统一的过滤器注册配置（如设置 `enabled`、`urlPatterns`、`order` 等），避免重复代码。

### 3.2 自定义过滤器 `DefaultCommonsRequestLoggingFilter`

该类通过构造器注入 `RequestLoggingOptions`，并在 `init()` 方法中将配置同步到父类的各个 setter：

```java
public void init() {
    setIncludeClientInfo(options.isIncludeClientInfo());
    setIncludeHeaders(options.isIncludeHeaders());
    setIncludePayload(options.isIncludePayload());
    setMaxPayloadLength(options.getMaxPayloadLength());
    setBeforeMessagePrefix(options.getBeforeMessagePrefix());
    setBeforeMessageSuffix(options.getBeforeMessageSuffix());
    setAfterMessagePrefix(options.getAfterMessagePrefix());
    setAfterMessageSuffix(options.getAfterMessageSuffix());
}
```

**关键重写方法**：

- `createMessage(..., String suffix)`：若 `includeTimestamp == true`，则在原 suffix 前插入 `", timestamp=" + System.currentTimeMillis()`，使日志输出类似：  
  `请求前 [GET /api/user, timestamp=1746789123456]`
- `shouldLog(HttpServletRequest)`：始终返回 `true`。父类默认会根据日志级别判断（需要 Logger 为 DEBUG），此处强制所有请求均尝试记录，最终是否输出仍取决于 `CommonsRequestLoggingFilter` 内部对 `logger.isDebugEnabled()` 的检查。

### 3.3 自动配置类 `WebMvcRequestLoggingConfiguration`

该配置类使用 `@Configuration(proxyBeanMethods = false)`，提供轻量级的配置 Bean。核心方法 `requestLoggingFilterRegistration` 完成：

1. 获取 `RequestLoggingOptions` 及其内部的 `ServletFilterOptions`。
2. 实例化 `DefaultCommonsRequestLoggingFilter` 并调用 `init()`。
3. 创建 `FilterRegistrationBean`，利用 `ServletFilterOptions.fill(registration)` 方法填充注册信息（如 `setEnabled`、`setUrlPatterns`、`setOrder` 等）。
4. 如果过滤器启用，则主动获取 `CommonsRequestLoggingFilter` 的 Logger 并调用 `isEnabledForLevel(Level.DEBUG)`（该语句实际未改变日志级别，仅用于触发日志框架的状态初始化，属于框架适配的防御性代码）。
5. 输出配置加载完成的 debug 日志。

## 4. 使用示例

### 4.1 Maven 依赖（假设已引入 tutorials4j 框架）

确保项目中包含 `spring-boot-starter-web` 及该框架模块。

### 4.2 配置文件 `application.yml`

```yaml
tutorials4j:
  web:
    http:
      request-logging:
        enabled: true                     # 通过 filter.enabled 控制
        filter:
          url-patterns: "/api/*"          # 仅拦截 API 路径
          order: 1
        include-timestamp: true
        include-client-info: true
        include-headers: true
        include-payload: true
        max-payload-length: 500
        before-message-prefix: ">>> "
        after-message-prefix: "<<< "
```

### 4.3 日志输出示例（Level = DEBUG）

```
>>> GET /api/user/123, client=0:0:0:0:0:0:0:1, headers=[User-Agent: Mozilla/5.0...], timestamp=1746789123456
<<< GET /api/user/123, status=200, timestamp=1746789123789
```

## 5. 扩展与定制

### 5.1 动态控制日志记录

当前 `shouldLog()` 固定返回 `true`，如需根据请求特征（如 URL、IP、用户角色）动态决定是否记录，可进一步重写该方法：

```java
@Override
protected boolean shouldLog(HttpServletRequest request) {
    return options.isEnabled() && !request.getRequestURI().contains("/health");
}
```

### 5.2 添加更多元数据

在 `createMessage` 中除了时间戳，还可添加请求耗时、TraceId 等信息：

```java
String traceId = MDC.get("traceId");
if (traceId != null) {
    newsuffix = ", traceId=" + traceId + newsuffix;
}
```

### 5.3 自定义日志输出目标

`CommonsRequestLoggingFilter` 默认使用 `org.springframework.web.filter.CommonsRequestLoggingFilter` 为 Logger 名。可以重写 `getLogger()` 方法，指定独立的 Logger 名称，以便分离日志文件。

## 6. 注意事项

- **性能影响**：记录请求体（`includePayload=true`）时，会读取 `InputStream` 并可能缓存，对大型请求有内存开销。合理设置 `maxPayloadLength` 并避免记录文件上传接口。
- **敏感信息**：请求头或 body 中可能包含密码、Token 等敏感数据，生产环境应关闭 `includeHeaders` 和 `includePayload`，或通过自定义 `createMessage` 进行脱敏。
- **日志级别**：`CommonsRequestLoggingFilter` 内部使用 `logger.isDebugEnabled()` 判断是否输出。因此必须将对应 Logger 的级别设为 `DEBUG`，例如在 `application.yml`：
  ```yaml
  logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
  ```

## 7. 总结

本文介绍的请求日志组件基于 Spring Boot 的扩展机制，实现了：

- **配置驱动**：通过 `@ConfigurationProperties` 将繁杂的过滤器参数外部化。
- **功能增强**：支持时间戳、灵活的 message 格式和强制日志记录策略。
- **集成友好**：通过 `FilterRegistrationBean` 注入，可与现有安全、监控过滤器链无缝协作。

该组件适用于需要统一请求日志规范的中大型项目，开发者可根据业务需求轻松调整其行为。完整代码已集成在 tutorials4j 框架中，开箱即用。