# security-2fa

## 代码功能分析

该项目基于 **Spring Boot 3** 和 **Google Authenticator** 库（`com.warrenstrange.googleauth`）实现了一个**两步验证（2FA）登录系统**。用户需要输入用户名、密码以及由 Google Authenticator 应用生成的动态验证码才能完成登录。

---

### 一、核心组件及职责

| 类名 | 职责 |
|------|------|
| `GoogleAuthConfig` | Spring 配置类，创建 `GoogleAuthenticator` Bean，并注入自定义的 `ICredentialRepository`（虽然该仓库实际未使用） |
| `GoogleAuthUtil` | 封装底层 Google Authenticator 操作：生成密钥、验证 TOTP 码、生成二维码 URL |
| `QRCodeUtil` | 工具类，根据二维码 URL 生成 PNG 图片字节数组 |
| `UserService` | 业务服务：管理用户密钥（生成并存入数据库）、验证密码和 2FA 验证码 |
| `AuthController` | REST 控制器：提供登录接口 (`/auth/login`) 和二维码生成接口 (`/auth/generate-qr`) |
| `PageController` | 视图控制器：返回 `2fa-setup` 页面（用于前端展示二维码及引导用户绑定） |
| `InMemoryCredentialRepository` | 实现了 `ICredentialRepository`，但注释明确说明 **未实际使用**（密钥直接存储在 `User` 表） |

---

### 二、工作流程

#### 1. 用户首次开启两步验证（绑定阶段）

- 前端访问 `/2fa-setup` 页面，页面中通过 AJAX 调用 `/auth/generate-qr?username=xxx` 获取二维码图片。
- `AuthController.generateQRCode()` 执行：
    - 调用 `userService.createSecretKeyForUser(username)`  
      → 生成新密钥（`GoogleAuthUtil.generateSecretKey()`）  
      → 查找 `User` 实体，设置 `secretKey` 并保存到数据库。
    - 调用 `googleAuthUtil.getQRBarcodeURL(username, secretKey)` 生成标准的 OTP Auth URL（格式：`otpauth://totp/...`）。
    - 利用 `QRCodeUtil.generateQRCode()` 将 URL 转为二维码图片字节数组返回。
- 前端展示二维码，用户使用 Google Authenticator 等应用扫描绑定。

#### 2. 用户登录（验证阶段）

- 前端提交 `username`、`password`、`code`（动态验证码）到 `/auth/login`。
- `AuthController.login()` 执行：
    - `userService.verifyPassword(username, password)`：从数据库获取用户，**明文比较密码**。
    - `userService.verifyCode(username, code)`：获取用户的 `secretKey`，调用 `googleAuthUtil.verifyCode(secretKey, code)` 验证 TOTP 码。
- 若两者均通过则返回成功，否则返回相应的 401 错误信息。

---

### 三、关键实现细节

- **密钥管理**：  
  密钥由 `GoogleAuthenticator.createCredentials()` 生成（通常为 Base32 编码的字符串）。系统将密钥直接存储在业务 `User` 表的 `secretKey` 字段中，**并未使用 `ICredentialRepository`**。因此 `InMemoryCredentialRepository` 是一个冗余 Bean，仅满足 `GoogleAuthConfig` 构造依赖，实际上从未被 `GoogleAuthenticator` 内部调用（因为验证时显式传入了 `secretKey` 参数）。

- **TOTP 验证**：  
  `GoogleAuthenticator.authorize(secretKey, code)` 方法内部根据当前时间戳和密钥计算期望的验证码，与用户输入的 `code` 比对。支持时间窗口偏移（默认配置）。

- **二维码生成**：  
  使用 `net.glxn.qrgen` 库生成二维码图片，尺寸 250×250，格式 PNG。

- **密码校验**：  
  当前为**明文比较**（`password.equals(mybatisUser.getPassword())`），这是严重的安全缺陷。生产环境应使用 BCrypt、Argon2 等哈希算法。

---

### 四、潜在问题与改进建议

| 问题 | 严重性 | 建议 |
|------|--------|------|
| 密码明文存储与比对 | **高** | 使用 `PasswordEncoder`（如 `BCryptPasswordEncoder`）加密存储密码，验证时调用 `matches()` |
| 未使用 `ICredentialRepository` 但强行注入 | 低 | 移除 `InMemoryCredentialRepository` 类，并在 `GoogleAuthConfig` 中移除对该 Bean 的依赖（因为 `GoogleAuthenticator` 不需要 repository 也能工作） |
| 缺少会话/令牌管理 | 中 | 登录成功后应生成 Session 或 JWT，后续请求携带凭证；当前仅返回字符串“登录成功” |
| 没有处理用户不存在的情况 | 中 | `UserService.verifyPassword` 中当 `mybatisUser == null` 时返回 `false`，但后续 `verifyCode` 中若 `mybatisUser == null` 会抛出 `NullPointerException`（`mybatisUser.getSecretKey()`） |
| `createSecretKeyForUser` 未做重复生成保护 | 中 | 如果用户已有密钥，再次调用会覆盖旧密钥，导致原有绑定失效。应增加判断：若密钥已存在则直接返回现有密钥 |
| 验证码为 `int` 类型，不能处理前导零 | 低 | TOTP 验证码通常为 6 位数字，`int` 会丢失前导零（如 `012345` → `12345`），应使用 `String` 类型 |
| 日志与异常处理缺失 | 中 | 添加日志记录关键操作（生成密钥、登录失败等），捕获可能异常并返回友好错误信息 |

---

### 五、总结

该系统实现了一个**完整的 TOTP 两步验证登录流程**，核心逻辑正确，二维码生成、验证码校验功能均能正常工作。但代码在**安全性**（密码明文）、**健壮性**（空指针、重复绑定）和**工程完整性**（会话管理、日志）方面存在明显不足，仅适合学习演示或原型阶段。生产环境需要按照上述建议进行重构。

