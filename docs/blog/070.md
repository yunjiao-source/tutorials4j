# [070][Web模块]Spring MVC TOTP 二次认证拦截器：设计与源码深度解析

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

## 1. 引言

在现代 Web 应用中，仅凭账号密码进行身份验证已难以满足安全需求。基于时间的一次性密码（TOTP, Time-Based One-Time Password）作为多因素认证（MFA）的常见形式，被广泛应用于银行、支付、后台管理等敏感场景。本文将基于一套实际项目中的 `TotpAuthHandlerInterceptor` 拦截器及 `@TotpAuth` 注解源码，深入剖析如何在 Spring MVC 框架中优雅地实现 TOTP 二次认证，并探讨其设计思路、实现细节及潜在改进方向。

## 2. 整体功能概述

该拦截器的作用是：在 Spring Web 请求处理流程中，对标注了 `@TotpAuth` 注解的 Controller 方法（或类）进行 TOTP 验证。验证所需信息（用户名、动态验证码）从 HTTP 请求头中获取，并通过 `GoogleAuthService` 完成校验。若验证失败或参数缺失，则抛出特定业务异常，交由全局异常处理器统一响应。

**核心组件**：
- `@TotpAuth`：方法级或类级注解，用于声明需要 TOTP 认证的资源。
- `TotpAuthHandlerInterceptor`：拦截器实现，负责解析注解、提取请求头、调用验证服务。
- `GoogleAuthService`：底层 TOTP 验证服务（基于 Google Authenticator 算法），负责根据用户名获取密钥并校验验证码。
- `SecurityUtils` 与 `HeaderUtils`：工具类，分别用于获取当前登录用户和读取请求头。

## 3. 源码逐段解析

### 3.1 注解定义：`@TotpAuth`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TotpAuth {
    String userName() default DefaultConsts.HTTP_HEADER_TOTP_AUTH_USERNAME;
    String authCode() default DefaultConsts.HTTP_HEADER_TOTP_AUTH_CODE;
}
```

- **作用范围**：既可标注在方法上，也可标注在类上（通常与 `@Controller` 组合使用，表示该类下所有方法都需要认证）。
- **可配置属性**：允许自定义存放用户名和验证码的请求头名称。默认值来自常量类，例如 `X-TOTP-Username` 和 `X-TOTP-Code`。这为不同客户端（如 Web、移动端）提供了灵活的头部命名约定。

### 3.2 拦截器核心：`preHandle` 方法

```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 1. 获取方法上的 @TotpAuth 注解
    TotpAuth totpAuth = WebUtils.getHandlerMethodAnnotation(handler, TotpAuth.class);
    if (ObjectUtils.isNotEmpty(totpAuth)) {
        // 2. 提取用户名（优先从指定头部获取，若无则从安全上下文获取）
        String userName = HeaderUtils.getHeader(request, totpAuth.userName());
        if (StringUtils.isBlank(userName)) {
            userName = SecurityUtils.getAccount();
        }
        // 3. 提取验证码
        String code = HeaderUtils.getHeader(request, totpAuth.authCode());

        // 4. 参数非空校验
        if (StringUtils.isAnyBlank(userName, code)) {
            throw WebErrorCode.WEB_TOTP_PARAMETERS_INCOMPLETE.throwed();
        }

        // 5. 调用 TOTP 验证服务
        if (!googleAuthService.verifyByUserName(userName, Integer.parseInt(code))) {
            throw WebErrorCode.WEB_TOTP_VERIFY_FAILURE.throwed();
        }
    }
    return true;
}
```

#### 细节剖析

**① 获取注解的方式**  
`WebUtils.getHandlerMethodAnnotation(handler, TotpAuth.class)` 是一个自定义工具方法，它封装了 Spring 的 `HandlerMethod` 获取逻辑，能够从 `handler` 对象中解析出方法或类上的注解，并优先方法级覆盖类级。这种设计使得开发者可以在 Controller 类上统一声明，又能在特定方法上灵活覆盖。

**② 用户名提取策略**  
- 首先从请求头中读取指定名称（默认 `X-TOTP-Username`）的值。  
- 若头部缺失或为空，则回退到 `SecurityUtils.getAccount()`。该方法通常从当前线程绑定的安全上下文（如 Spring Security 的 `Authentication`）中获取已认证的用户名。  
- **设计意图**：支持两种场景：一是客户端直接携带用户名（适用于非登录态接口，如二次验证时前端主动提供）；二是利用已有登录上下文（适用于已登录用户进行 TOTP 验证）。这种降级策略增强了灵活性，但也需注意安全风险——若客户端可随意伪造头部，则可能绕过用户名校验，因此建议仅在内部网络或配合签名机制使用。

**③ 验证码格式转换**  
`Integer.parseInt(code)` 直接转换为整数。但 TOTP 验证码通常为 6 位数字，可能以字符串形式传递，此处未做长度或格式校验，若头部包含非数字字符将抛出 `NumberFormatException`，应被全局异常捕获并转为友好错误。建议增强校验逻辑。

**④ 验证服务调用**  
`googleAuthService.verifyByUserName(userName, Integer.parseInt(code))` 是核心验证点。该服务内部会根据用户名获取对应的 TOTP 密钥（通常存储在数据库或缓存中），然后基于当前时间窗口（如 30 秒）计算期望的验证码并与输入比对。若失败，抛出 `WEB_TOTP_VERIFY_FAILURE`。

**⑤ 日志记录**  
仅在 debug 级别记录请求方法和 URI，便于追踪问题，同时避免生产环境大量日志。

## 4. 工作流程与集成方式

### 4.1 配置拦截器

需在 Spring MVC 配置中注册该拦截器，并指定拦截路径（通常为所有请求或特定 API 前缀）：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private TotpAuthHandlerInterceptor totpAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(totpAuthInterceptor)
                .addPathPatterns("/api/**");
    }
}
```

### 4.2 使用注解

```java
@RestController
@RequestMapping("/admin")
@TotpAuth  // 类级别，所有方法均需 TOTP
public class AdminController {

    @GetMapping("/settings")
    public String getSettings() { ... }

    @PostMapping("/publish")
    @TotpAuth(authCode = "X-OTP-CODE") // 可覆盖默认头部
    public void publish() { ... }
}
```

## 5. 异常处理与响应

拦截器中抛出的 `WebErrorCode.WEB_TOTP_PARAMETERS_INCOMPLETE` 和 `WEB_TOTP_VERIFY_FAILURE` 是自定义业务错误码，应由 `@ControllerAdvice` 全局异常处理器统一捕获，转换为标准的 HTTP 响应（如 401 Unauthorized 或 403 Forbidden），并附带错误信息供前端展示。

## 6. 潜在问题与优化建议

### 6.1 安全风险

- **用户名可被伪造**：若头部 `userName` 被恶意篡改，而 `SecurityUtils.getAccount()` 又未生效（如未登录），则攻击者可能用他人用户名尝试爆破验证码。建议强制要求 `SecurityUtils.getAccount()` 存在，且与头部用户名一致（若头部存在），否则拒绝。
- **验证码重放攻击**：TOTP 本身有时效性，但拦截器未做防重放措施（如一次性使用标记）。建议结合业务场景，对同一用户短时间内的连续失败进行限制。
- **HTTP 明文传输**：TOTP 验证码在请求头中传输，若未使用 HTTPS，可能被中间人截获。生产环境必须强制 TLS。

### 6.2 性能考虑

- `googleAuthService.verifyByUserName` 可能涉及数据库查询和 SHA-1 哈希计算，建议对用户密钥进行缓存，降低延迟。
- 对于高并发接口，可考虑异步验证或使用本地缓存。

### 6.3 代码健壮性

- `Integer.parseInt(code)` 应替换为更安全的解析方法，或增加 `@Digits` 校验，避免因非数字内容导致服务器异常。
- 注解中的 `userName()` 和 `authCode()` 头部名称如果通过常量引用，可确保全局统一。

### 6.4 扩展性

- 当前仅支持 TOTP，未来若需支持 HOTP 或短信验证码，可抽象 `AuthService` 接口，通过策略模式动态选择。
- 注解可增加 `required` 属性，允许在某些环境中跳过验证（如开发环境）。

## 7. 总结

`TotpAuthHandlerInterceptor` 以极简的代码实现了 TOTP 二次认证的拦截逻辑，充分利用了 Spring MVC 的拦截器机制和注解驱动开发范式。它通过请求头获取参数，兼顾灵活性与扩展性，同时依赖安全上下文提供降级方案。然而，在实际生产环境中，仍需关注用户名可信性、输入校验、传输加密及防重放等安全细节。本文的分析希望能为读者实现类似功能提供参考，也启发大家在安全设计中兼顾易用性与严谨性。

---

> 文章作者：基于提供的代码片段分析生成  
> 技术栈：Spring MVC 6.x / Jakarta Servlet / Lombok / Google Authenticator  
> 适用读者：Java Web 开发者、安全架构师