该代码实现了一个基于 Spring Boot 的 API 接口自动加解密组件，核心功能是对标记了 `@Crypto` 注解的控制器方法，自动解密请求体（`@RequestBody` 参数）并加密响应体（统一为 `Result` 类型），从而简化前后端传输敏感数据时的加解密处理。

## 主要功能模块

### 1. 注解控制（`@Crypto`）
- 作用于 Controller 方法
- 属性 `request`（是否解密请求体，默认 true）和 `response`（是否加密响应体，默认 true）

### 2. 配置管理（`CryptoProperties` + `CryptoAutoConfiguration`）
- 前缀 `crypto`，需配置 `crypto.enabled=true` 开启功能
- 支持算法选择（默认 AES），需提供 `crypto.aes-key`（Base64 格式的 AES 密钥）
- 自动配置 `CryptoProcessor`（目前仅 AES）、`CryptoRequestBodyAdvice`、`CryptoResponseBodyAdvice`

### 3. 加解密处理器（`CryptoProcessor` + `AESCryptoProcessor`）
- 使用 **AES/CBC/PKCS5Padding** 算法
- IV 取自密钥的前 16 字节
- 输入/输出均为字符串：加密返回 Base64 密文，解密输入 Base64 密文返回明文

### 4. 请求体解密（`CryptoRequestBodyAdvice`）
- 实现 `RequestBodyAdvice`，全局拦截请求
- 判断条件：全局开启 + 方法有 `@Crypto` 且 `request=true`
- 读取原始请求体字符串（应为 Base64 密文），解密后验证是否为合法 JSON，然后替换为解密后的请求体供后续 Controller 使用

### 5. 响应体加密（`CryptoResponseBodyAdvice`）
- 实现 `ResponseBodyAdvice`，全局拦截响应
- 判断条件：全局开启 +（方法有 `@Crypto` 且 `response=true` 或为异常响应）
- 仅对 `Result` 类型的响应体进行加密：将 `Result` 对象序列化为 JSON，加密后返回密文字符串，并添加响应头 `x-encrypt-response` 和 `x-encrypt-error`（值为 `AES`）

### 6. 异常处理
- 自定义 `CryptoException`，加解密失败时抛出

### 7. 示例控制器（`CryptoUserController`）
- 演示如何使用 `@Crypto` 注解保护 `/login` 接口
- 接收 `UserDto`，返回 `Result<String>`，自动完成请求解密和响应加密

## 工作流程
1. **请求**：客户端发送 Base64 密文作为请求体 → 解密为 JSON → 绑定到 `@RequestBody` 参数
2. **响应**：Controller 返回 `Result` 对象 → 序列化为 JSON → 加密为 Base64 密文 → 客户端收到

## 注意事项
- 请求体必须是纯密文字符串（不能包含额外 JSON 结构），解密后必须为合法的 JSON 格式
- 响应加密仅针对 `Result` 类型，如需支持其他返回类型可扩展
- 异常响应的判断逻辑（`isExceptionResponse`）较为简单，实际使用时可能需要优化
- IV 固定使用密钥前 16 字节，密钥需妥善保管，建议使用 32 字节（AES-256）并确保 Base64 编码正确

该组件为 Spring Boot 3 项目提供了灵活、低侵入的接口加解密能力，适用于需要对 API 传输数据加密的场景。