# [040][验证码模块]验证码请求过滤器（CaptchaRequestFilter）设计与实现解析

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在Web应用安全领域，验证码是抵御自动化攻击、暴力破解、垃圾注册等行为的常见手段。然而，若每个需要验证码的业务接口都手动编写校验逻辑，会造成大量重复代码，且容易遗漏校验点。本文介绍的 `CaptchaRequestFilter` 是一个基于Servlet过滤器的统一验证码校验组件，它通过拦截请求、提取请求头中的验证码参数、调用验证码服务完成校验，并将验证码相关请求头安全移除，从而实现了验证码校验与业务逻辑的解耦。

## 一、整体职责与定位

`CaptchaRequestFilter` 继承自 Spring Web 提供的 `OncePerRequestFilter`，确保每个请求仅经过一次过滤。它的核心职责包括：

1. **参数提取**：从HTTP请求头中读取验证码的唯一标识 `key`、类别 `category` 以及用户输入的 `code`。
2. **完整性校验**：检查上述三个参数是否缺失，若缺失则直接抛出异常，快速失败。
3. **验证码校验**：根据 `category` 从 `CaptchaServiceFactory` 获取对应的验证码服务实现，调用其 `verify(key, code)` 方法进行校验。
4. **请求头清理**：校验通过后，将原始请求包装为移除验证码请求头的装饰器对象，再传递给后续过滤器链，防止验证码参数透传到业务层。

整个过滤器采用“不通过则中止”的策略，一旦校验失败，业务逻辑完全不会执行，有效保障了后端接口的安全。

## 二、依赖组件详解

为了理解过滤器的运作流程，需要先了解与其协作的几个关键组件。

### 1. `CaptchaCategory` 枚举

定义了系统支持的所有验证码类型，包括天爱验证码（滑动还原、旋转、点选、滑块）以及 Hutool 验证码（线段干扰、圆圈干扰、扭曲干扰、GIF）。每个枚举值关联了图片扩展名和描述信息。

```java
public enum CaptchaCategory {
    TIANAI_CONCAT("png", "滑动还原验证码"),
    HUTOOL_LINE("png", "线段干扰验证码"),
    // ... 其他类型
}
```

过滤器从请求头中读取的 `category` 字符串最终会被映射为 `CaptchaCategory` 枚举值，用于定位具体的验证码服务。

### 2. `CaptchaService` 接口

定义了验证码服务的统一契约：

```java
public interface CaptchaService {
    Map<String, Object> draw();          // 生成验证码（图片+key）
    boolean verify(String key, String userCode); // 校验
    CaptchaCategory getCategory();       // 返回自身支持的类别
}
```

不同的验证码实现（如天爱滑块验证码、Hutool图形验证码）均实现该接口，并注册为 Spring Bean。

### 3. `CaptchaServiceFactory` 工厂

这是一个 Java `record`，内部维护了一个 `Map<CaptchaCategory, CaptchaService>`。它提供两个查找方法：

- `findService(String categoryName)`：先将字符串转换为 `CaptchaCategory` 枚举（使用 `EnumUtils.getEnum`），若转换失败或映射中不存在对应服务则抛出 `CaptchaException`。
- `findService(CaptchaCategory category)`：直接从 Map 中获取服务实例。

工厂的存在使得过滤器无需知晓具体有哪些验证码实现，只需根据类别名称即可获得对应的校验能力。

### 4. `RemoveHeaderRequestWrapper`

这是一个自定义的 `HttpServletRequestWrapper` 实现。它接受一个请求对象和一个需要移除的请求头名称，在重写的 `getHeader`、`getHeaderNames` 等方法中过滤掉该请求头。过滤器通过它将 `DefaultConsts.HTTP_HEADER_CAPTCHA`）对应的请求头全部移除。

## 三、过滤器工作流程详述

下面以一次典型的请求为例，描述过滤器的执行步骤。

### 步骤1：进入过滤器

客户端发送一个需要验证码保护的请求（例如登录、注册），请求头中必须携带三个参数：

| 请求头名称                  | 含义                 | 示例值                 |
| --------------------------- | -------------------- | ---------------------- |
| `X-Captcha-Key` (常量)      | 验证码唯一标识       | `abc123-def456`        |
| `X-Captcha-Category` (常量) | 验证码类型           | `HUTOOL_LINE`          |
| `X-Captcha-Code` (常量)     | 用户输入的验证码内容 | `8F3A`                 |

### 步骤2：参数校验

过滤器调用 `StringUtils.isAnyBlank(key, category, code)` 检查三者是否都存在。只要有一个为 `null` 或空字符串，立即抛出 `CaptchaException("验证码参数不完整")`。该异常通常会被全局异常处理器捕获，返回 `400 Bad Request` 或自定义错误响应。

### 步骤3：调用验证码服务

- 通过 `captchaServiceFactory.findService(category)` 获取对应类别的 `CaptchaService` 实例。
- 调用 `service.verify(key, code)` 执行校验。不同的验证码实现可能有不同的校验逻辑（例如滑块验证码需要验证轨迹、点选验证码需要比对坐标等），但对过滤器而言完全透明。

如果 `verify` 返回 `false`，则抛出 `CaptchaException("验证码校验失败")`，请求终止。

### 步骤4：清理请求头并继续

校验通过后，过滤器创建一个 `RemoveHeaderRequestWrapper` 包装原始请求，指定要移除的请求头名称 `DefaultConsts.HTTP_HEADER_CAPTCHA`。这样做是为了避免业务控制器再次收到验证码相关的头信息（业务层不需要关心验证码，只需要处理业务数据）。

最后，调用 `filterChain.doFilter(wrapper, response)` 将包装后的请求向下传递。后续的 Filter 和 Controller 获取到的请求对象将不再包含验证码请求头。

## 四、过滤器注册与配置

过滤器的注册由 `CaptchaConfiguration` 自动配置类完成：

```java
@Bean
FilterRegistrationBean<CaptchaRequestFilter> traceRequestFilterRegistration(
        CaptchaServiceFactory captchaServiceFactory, CaptchaProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<CaptchaRequestFilter> registration = new FilterRegistrationBean<>();
    CaptchaRequestFilter filter = new CaptchaRequestFilter(captchaServiceFactory);
    registration.setFilter(filter);
    options.fill(registration); // 设置urlPatterns, order等
    return registration;
}
```

- `CaptchaProperties` 中包含了 `filter` 配置项（如 `urlPatterns`、`order`），开发者可通过配置文件定制哪些接口需要验证码校验，以及过滤器的执行顺序。
- 默认情况下，过滤器会对所有匹配的路径生效。

例如，在 `application.yml` 中：

```yaml
tutorials4j:
  captcha:
    filter:
      url-patterns: /api/login, /api/register
      order: 1
```

## 五、设计亮点与优势

### 1. 关注点分离

业务控制器完全不需要编写任何验证码校验代码，只需关注自身逻辑。验证码的生成、存储、校验全部下沉到框架层。

### 2. 请求头驱动

使用请求头传递验证码参数，而不是表单字段或JSON体，好处是：
- 与业务数据解耦，避免侵入POJO。
- 可以统一在网关层或过滤器层处理，甚至可以在反向代理层面提前校验。
- 验证码参数被移除后，业务层完全感知不到验证码的存在。

### 3. 可扩展的验证码工厂

通过 `CaptchaServiceFactory` 和 `CaptchaCategory` 枚举，新增一种验证码类型只需：
- 添加一个枚举值。
- 实现 `CaptchaService` 并注册为 Spring Bean。

无需修改过滤器任何代码，符合开闭原则。

### 4. 快速失败机制

在参数不完整或校验失败时立即抛出异常，避免执行后续昂贵的业务逻辑（如数据库查询、外部API调用），提升了系统整体性能与安全性。

## 六、使用示例与最佳实践

### 1. 客户端请求示例

```http
POST /api/login HTTP/1.1
Host: example.com
X-Captcha-Key: 550e8400-e29b-41d4-a716-446655440000
X-Captcha-Category: HUTOOL_LINE
X-Captcha-Code: 8F3A
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "********"
}
```

### 2. 业务控制器示例

```java
@RestController
public class LoginController {
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 验证码已经由过滤器校验通过，这里直接处理登录逻辑
        // 注意：HttpServletRequest 中已经没有 X-Captcha-* 头
        return ok();
    }
}
```

### 3. 异常处理建议

由于过滤器会抛出 `CaptchaException`，建议配合 `@ControllerAdvice` 进行统一异常处理：

```java
@ExceptionHandler(CaptchaException.class)
public ResponseEntity<ErrorResponse> handleCaptchaException(CaptchaException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
}
```

## 七、潜在改进点

尽管当前设计已经相当优秀，仍有几个方向可以继续优化：

1. **支持多种传递方式**：除了请求头，可配置是否允许从请求参数（Query String）或表单字段中读取验证码。
2. **校验失败后的重试机制**：当前失败即终止，对于滑块验证码等交互式验证，可能需要返回新的验证码图片。
3. **监控与指标**：增加验证码校验通过/失败的计量指标，便于运维观察攻击趋势。
4. **柔性校验**：对于某些非核心接口，可配置校验失败后只记录日志而不阻断请求（降级模式）。

## 八、总结

`CaptchaRequestFilter` 是一个设计清晰、职责单一的Web过滤器，它巧妙地利用了Spring的过滤器链、请求包装器以及工厂模式，为应用提供了统一的、可插拔的验证码校验能力。通过该过滤器，开发者可以集中管理验证码策略，业务代码得以保持整洁，且整体安全性显著提升。

在微服务架构中，该过滤器甚至可以前置到网关层（如Spring Cloud Gateway），实现全局的验证码防护。无论是传统单体应用还是分布式系统，`CaptchaRequestFilter` 的设计思路都极具参考价值。