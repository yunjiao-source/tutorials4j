# [014][web模块]构建可重复读取的请求体：Spring Boot 请求缓存过滤器设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 一、背景与痛点分析

在 Java Web 开发中，`HttpServletRequest` 的请求体（Body）默认是基于流的，这意味着它**只能被读取一次**。一旦调用了 `getInputStream()` 或 `getReader()` 方法，原始流中的数据就会被消耗殆尽，后续再调用这些方法将无法再次获取数据。

这种设计在许多场景下会带来问题：

- **参数校验与日志记录**：过滤器需要读取请求体进行日志记录，而 Controller 又需要读取参数进行业务处理。
- **签名验证**：验证请求体签名时需要读取一次，业务处理时还需要再读取一次。
- **请求转发与重试**：在某些网关或代理场景下，需要对请求体进行多次分析。

为了解决“一次读取”的限制，业界常见的做法是通过 **HttpServletRequestWrapper** 对原始请求进行包装，并在包装时提前将请求体内容缓存下来。本文将详细分析一套完整的请求体缓存实现方案。

## 二、整体模块结构

该代码模块包含以下核心组件：

| 类名 | 职责描述 |
|------|----------|
| `CachedHttpServletRequestWrapper` | 核心包装类，继承 `HttpServletRequestWrapper`，在构造时读取并缓存原始请求体。 |
| `CachedServletInputStream` | 自定义的 `ServletInputStream` 实现，基于字节数组提供可重复读取的输入流。 |
| `CachedRequestFilter` | Servlet 过滤器，负责判断是否需要将原始请求包装为缓存包装类。 |
| `CachedRequestConfiguration` | Spring Boot 自动配置类，用于注册过滤器并读取配置属性。 |
| `WebHttpProperties` | 配置属性绑定类，支持用户自定义 URL 匹配模式、最大请求体长度、过滤器顺序等。 |

整体流程如下图所示（文字描述）：

```
客户端请求 → CachedRequestFilter → 检查 Content-Length/是否已包装 
→ 未包装且长度合法 → new CachedHttpServletRequestWrapper（缓存请求体）
→ 链条传递包装后的请求 → 后续任意次数调用 getInputStream/getReader 均可成功
```

## 三、核心实现详解

### 1. 请求体缓存包装器：CachedHttpServletRequestWrapper

该类是整个方案的基石。它在构造时完成请求体的读取和缓存：

```java
public CachedHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    String charset = request.getCharacterEncoding();
    if (charset == null) charset = StandardCharsets.UTF_8.name();
    try (BufferedReader reader = request.getReader()) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        cachedBody = sb.toString().getBytes(charset);
    }
}
```

**设计要点：**
- 优先使用请求自带的字符编码，若未指定则默认 UTF-8。
- 一次性将整个请求体读入内存中的 `byte[]` 数组。
- 重写 `getInputStream()` 和 `getReader()`，每次调用都基于缓存的字节数组创建新的流实例。

**潜在风险：** 大请求体可能导致内存溢出。解决方案见后续配置中的 `maxContentLength` 限制。

### 2. 可重复读的输入流：CachedServletInputStream

该类包装了 `ByteArrayInputStream`，并实现了 `ServletInputStream` 的必要方法：

```java
@Override
public boolean isFinished() {
    return cachedBodyInputStream.available() == 0;
}

@Override
public boolean isReady() {
    return true;  // 内存数据总是就绪
}

@Override
public void setReadListener(ReadListener readListener) {
    throw new UnsupportedOperationException("不支持异步非阻塞读取");
}
```

**设计说明：**
- `isReady()` 永远返回 `true`，因为数据完全在内存中，无需等待 I/O。
- 不支持异步非阻塞模式，对于绝大多数同步 Web 应用这完全可以接受。
- `read()`、`read(byte[], int, int)`、`close()` 全部委托给内部的 `ByteArrayInputStream`。

### 3. 过滤器：CachedRequestFilter

过滤器负责条件化地进行包装，避免滥用和重复包装：

```java
if (!(httpRequest instanceof CachedHttpServletRequestWrapper)) {
    int length = httpRequest.getContentLength();
    if (length > cachedRequest.getMaxContentLength().toBytes()) {
        log.warn("请求体长度超过最大值，放弃包装");
    } else {
        httpRequest = new CachedHttpServletRequestWrapper(httpRequest);
    }
}
chain.doFilter(httpRequest, response);
```

**关键逻辑：**
1. **避免重复包装**：检查当前请求是否已经是包装类实例。
2. **长度检查**：若 `Content-Length` 超过配置的最大长度（默认 2MB），则放弃包装并记录警告。
3. **降级处理**：放弃包装后，原请求继续传递，只是后续无法重复读取（符合最小惊讶原则）。

### 4. Spring Boot 自动配置：CachedRequestConfiguration

通过 `FilterRegistrationBean` 将过滤器注册到 Servlet 容器中，并支持以下自定义配置项（通过 `application.yml` 设置）：

```yaml
tutorials4j:
  web:
    http:
      cached-request:
        enabled: true                 # 是否启用
        url-patterns: /api/*, /cached/*  # 匹配的URL模式
        max-content-length: 4MB       # 最大缓存请求体大小
        order: 1                      # 过滤器执行顺序
        name: myCachedRequestFilter
```

## 四、使用示例与效果验证

### 启用方式

确保包含该模块的 jar 包在 classpath 中，Spring Boot 会自动读取 `CachedRequestConfiguration`。若不需要自动配置，也可以通过条件注解排除。

### 验证多次读取

在 Filter 或 Interceptor 中：

```java
// 第一次读取
String body1 = getBodyAsString(request);
// 第二次读取（若未包装则会抛异常）
String body2 = getBodyAsString(request);
log.info("读取结果一致：{}", body1.equals(body2));
```

辅助方法：

```java
private String getBodyAsString(HttpServletRequest request) throws IOException {
    try (BufferedReader reader = request.getReader()) {
        return reader.lines().collect(Collectors.joining());
    }
}
```

如果请求被成功包装，两次读取会得到完全相同的内容；若因长度过大而放弃包装，第二次读取将抛出 `IllegalStateException`。

## 五、设计权衡与注意事项

### 优点

- **透明集成**：对业务代码零侵入，只需放置过滤器即可。
- **灵活配置**：支持路径匹配、长度限制、过滤器顺序等精细控制。
- **内存效率可控**：通过 `maxContentLength` 防止大请求耗尽内存。
- **标准 API 兼容**：完全符合 Servlet 规范对 `getInputStream/getReader` 的契约。

### 注意事项

1. **仅适用于小/中等大小请求体**。超大文件上传场景不应缓存，应使用流式处理。
2. **字符编码问题**：`getReader()` 方法使用了平台默认字符集，生产环境中建议统一编码或直接从 `getInputStream` 读取并指定字符集。
3. **异步非阻塞不支持**：若应用重度使用 Servlet 异步 I/O，需要扩展 `setReadListener` 实现。
4. **Content-Length 未必可靠**：某些分块传输（chunked）请求中 `getContentLength()` 返回 -1，此时无法做长度预检。如需支持可改为读取前几个字节估算或不做限制。

## 六、扩展建议

- **支持文件上传（multipart）场景**：可结合 Commons FileUpload 或 Spring 的 `MultipartResolver`，在缓存同时保留解析后的文件项。
- **增加请求体大小警告**：当超过阈值（如 1MB）但未达到最大限制时，记录警告便于运维监控。
- **提供显式清理 API**：在超长生命周期的请求上下文中，允许手动释放 `cachedBody` 引用，辅助 GC。

## 七、总结

本文分析的 `CachedHttpServletRequestWrapper` 是一个轻量、高效且实用的请求体缓存方案。它通过包装模式、字节数组缓存和条件过滤，优雅地解决了 HttpServletRequest 请求体只能读取一次的限制问题。配合 Spring Boot 的自动配置特性，开发者可以在几乎不修改原有业务代码的前提下，获得请求体的多次读取能力，极大地方便了日志审计、签名验签、参数预处理等中间件的开发。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e