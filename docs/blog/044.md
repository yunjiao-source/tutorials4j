# [044][Web模块]基于 Google Authenticator 的 TOTP 双因素认证框架设计与实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

---

在 Web 应用安全领域，双因素认证（2FA）已成为保护用户账户的常见手段。TOTP（基于时间的一次性密码算法）是其中一种主流实现，而 Google Authenticator 则是其经典客户端。本文将介绍一个 Spring Boot 集成方案——`tutorials4j-framework-web-google-auth`，它封装了 TOTP 服务的自动配置、请求拦截、管理接口等能力，提供开箱即用的双因素认证功能。

## 一、整体架构与功能概览

该框架基于 `com.warrenstrange.googleauth` 库，专为 Spring Boot 3.x 设计，包含以下核心能力：

- **自动配置**：根据条件自动创建 `GoogleAuthenticator` 实例并注入必要的依赖。
- **TOTP 业务服务**：封装密钥生成、验证码校验、二维码 URL 生成。
- **请求过滤器**：统一拦截需要 2FA 保护的请求，从请求头读取用户名和验证码并执行校验。
- **管理接口**：提供 REST API 用于生成绑定二维码、测试校验。
- **凭证仓库扩展**：允许从 YAML 配置文件或自定义数据源（如数据库）读取用户密钥。
- **配置定制**：支持调整 TOTP 算法参数（时间步长、窗口大小等）。

下图展示了该框架在 Spring Boot 应用中的位置：

```
用户请求 → [GoogleAuthRequestFilter] → 校验 TOTP → 业务处理
                ↓ 失败
            抛出异常
```

## 二、核心组件分析

### 1. 自动配置类 `GoogleAuthWebConfiguration`

该类在类路径存在 `GoogleAuthenticator` 时生效，负责装配以下 Bean：

| Bean | 作用 |
|------|------|
| `GoogleAuthenticator` | 核心 TOTP 算法实现，绑定自定义凭证仓库 |
| `XICredentialRepository` | 默认使用 YAML 配置仓库，可被覆盖 |
| `GoogleAuthService` | 业务服务门面 |
| `FilterRegistrationBean<GoogleAuthRequestFilter>` | 注册过滤器，支持自定义匹配路径和顺序 |

关键代码片段：

```java
@Bean
GoogleAuthenticator googleAuthenticator(XICredentialRepository repository,
                                        ObjectProvider<GoogleAuthenticatorConfigCustomizer> customizers) {
    GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.Builder().build();
    customizers.orderedStream().forEach(customizer -> customizer.customize(config));
    GoogleAuthenticator authenticator = new GoogleAuthenticator(config);
    authenticator.setCredentialRepository(repository);
    return authenticator;
}
```

### 2. 凭证仓库接口 `XICredentialRepository`

继承自 `ICredentialRepository`，增加了密码校验方法，为两步验证（密码+TOTP）提供扩展点。

```java
public interface XICredentialRepository extends ICredentialRepository {
    boolean verifyPassword(String userName, String password);
}
```

框架提供了基于 YAML 的内存实现 `YamlCredentialRepository`，适合小规模固定用户场景。生产环境可自行实现数据库版本。

### 3. TOTP 业务服务 `GoogleAuthService`

封装了底层库的调用，简化使用：

- `generateSecretKey()`：生成随机秘钥。
- `verifyByUserName()` / `verifyBySecretKey()`：校验验证码。
- `getQRBarcodeURL()`：生成 `otpauth://` 协议 URL，用于二维码生成。

### 4. 请求过滤器 `GoogleAuthRequestFilter`

继承 `OncePerRequestFilter`，从请求头中获取：

- `X-Google-Auth-Username`（或从 `SecurityUtils.getAccount()` 获取）
- `X-Google-Auth-Code`

校验失败抛出 `WebFrameworkException`，由全局异常处理器转换为 HTTP 响应。校验通过后，将验证码请求头从 `RemoveHeaderRequestWrapper` 中移除，避免下游再次读取敏感信息。

### 5. 管理接口 `GoogleAuthController`

提供两个端点：

| 端点 | 方法 | 功能 |
|------|------|------|
| `/t4j/google-auth/check` | POST | 手动校验用户名和验证码 |
| `/t4j/google-auth/generate/qr` | GET | 为用户生成新秘钥并返回二维码图片（PNG） |

## 三、配置说明

在 `application.yml` 中配置如下：

```yaml
tutorials4j:
  web:
    google-auth:
      otp-auth-totp-url: "myapp"           # 二维码中显示的应用名称
      credentials:                          # 静态用户列表（仅用于 YAML 仓库）
        - username: admin
          password: admin123
          security-key:                     # 可选，若空则首次生成后自动填充内存
        - username: user1
          password: pass123
      filter:                               # 过滤器配置
        url-patterns:
          - "/api/secure/*"
        order: 1
        name: "googleAuthFilter"
```

- `otp-auth-totp-url`：影响二维码 URI 中的 issuer 参数，客户端扫描后显示为“myapp (username)”。
- `credentials`：仅当使用默认的 `YamlCredentialRepository` 时生效。注意 `security-key` 字段若为空，首次调用 `generateSecretKey` 会生成并保存在内存 Map 中，**不会写回配置文件**。生产环境建议实现数据库仓库。
- `filter`：通过 `ServletFilterOptions` 配置拦截路径、过滤器顺序和名称。

## 四、使用流程

### 1. 用户首次绑定

1. 前端调用 `GET /t4j/google-auth/generate/qr?username=admin`。
2. 后端生成新秘钥，并返回二维码图片。
3. 用户使用 Google Authenticator 等 App 扫描二维码，完成绑定。

### 2. 登录/受保护接口调用

客户端在后续请求的 Header 中携带：

```
X-Google-Auth-Username: admin
X-Google-Auth-Code: 123456
```

过滤器自动校验，通过后放行；校验失败抛出异常，前端可捕获并提示。

## 五、扩展与定制

### 1. 调整 TOTP 算法参数

实现 `GoogleAuthenticatorConfigCustomizer` Bean：

```java
@Component
public class MyConfigCustomizer implements GoogleAuthenticatorConfigCustomizer {
    @Override
    public void customize(GoogleAuthenticatorConfig config) {
        config.setTimeStepSizeInMillis(30000);  // 30秒步长
        config.setWindowSize(2);                // 允许前后一个时间窗口
    }
}
```

### 2. 自定义凭证仓库（例如数据库）

实现 `XICredentialRepository`，并声明为 `@Primary` 或 `@Component`，框架将自动使用你的实现而非 YAML 版本。

```java
@Component
public class DatabaseCredentialRepository implements XICredentialRepository {
    // 实现 getSecretKey, saveUserCredentials, verifyPassword
}
```

### 3. 动态获取当前用户名

默认过滤器优先从请求头获取用户名，若为空则调用 `SecurityUtils.getAccount()`。你可以修改该工具类以适配自己的认证上下文（如 Spring Security）。

## 六、总结

该框架通过 Spring Boot 自动配置将 Google Authenticator 无缝集成到 Web 应用中，具有以下优点：

- **开箱即用**：提供默认的 YAML 配置驱动，适合快速原型或小规模内网应用。
- **安全设计**：过滤器校验后自动删除验证码请求头，避免泄露；支持密码 + TOTP 双重校验扩展。
- **灵活扩展**：支持算法参数定制、凭证仓库热插拔，满足不同部署环境。
- **开发友好**：内置管理接口，方便测试和用户绑定。

在实际生产环境中，建议将凭证仓库改为数据库实现，并配合 Spring Security 等权限框架，将 TOTP 校验作为第二道防线。

通过本文的讲解，相信读者可以快速理解并集成该 TOTP 认证方案，构建更安全的 Web 应用。