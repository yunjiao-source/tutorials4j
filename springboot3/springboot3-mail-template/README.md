# springboot3-mail-template

该代码实现了一个基于 Spring Boot 3 的邮件发送服务，核心功能如下：

### 1. 邮件发送服务（`EmailService`）
- 使用 `JavaMailSender` 发送邮件，`TemplateEngine`（Thymeleaf）渲染 HTML 模板。
- `sendTemplateEmail` 方法接收收件人、主题、模板名称和 Thymeleaf 上下文，生成 MIME 邮件并发送。
- 发送前会记录邮件 HTML 内容到日志。

### 2. 控制器（`EmailController`）
- 提供 `POST /sendTemplateEmail` 接口，需要传入 `to`（收件人）和 `subject`（主题）参数。
- 创建 Thymeleaf 上下文，将 `name` 变量设置为固定值 `"John Doe"`。
- 调用 `EmailService` 发送模板邮件（模板名为 `email-template`），并返回成功消息。

### 3. 邮件模板（`email-template.html`）
- 使用 Thymeleaf 语法，展示 `name` 变量的值。
- 生成类似 “Hello, John Doe!” 的问候语及一段固定文本。

### 4. 配置文件（`application.properties`）
- 配置了 QQ 邮箱的 SMTP 服务器、用户名、密码（需填写授权码）、编码及 TLS 等参数。
- 声明了 `spring.mail.from` 属性，但代码中未使用，可能导致发件人缺失。

### 潜在问题与改进建议
- **发件人缺失**：应在 `MimeMessageHelper` 中调用 `setFrom(spring.mail.from)`。
- **异常处理**：控制器直接抛出 `MessagingException`，建议添加 `@ExceptionHandler` 或返回统一错误响应。
- **参数校验**：`@RequestParam` 未设置 `required` 或默认值，缺少参数会返回 400。
- **模板路径**：Thymeleaf 默认查找 `src/main/resources/templates/email-template.html`，需确保文件存在。
- **密码安全**：配置中的 `password` 建议使用环境变量或配置中心，避免明文存储。

### 整体功能
通过 REST API 触发，发送一封固定内容（仅名字可变）的 HTML 邮件，适用于通知、验证码等场景的模板化邮件发送示例。