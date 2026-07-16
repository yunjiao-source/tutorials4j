# [071][验证码模块]基于Spring拦截器的验证码认证设计思想

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

## 1. 背景与需求
在Web应用中，为防止暴力破解、机器刷票等恶意行为，验证码校验是常见的安全手段。传统的校验方式往往在业务代码中硬编码，导致逻辑分散、复用性差。本文介绍的方案通过**注解 + 拦截器**，将验证码认证与业务逻辑解耦，实现声明式的权限控制。

## 2. 整体架构
该方案包含三个核心组件：
- **`@CaptchaAuth` 注解**：标记需要校验的接口（类或方法），并声明校验所需的参数名（默认从HTTP头读取）。
- **`CaptchaAuthHandlerInterceptor` 拦截器**：在请求预处理阶段，检测注解并执行校验逻辑。
- **`CaptchaServiceFactory` 工厂**：根据`category`（验证码类型）获取对应的验证服务，支持多种验证码策略（如图形、短信、邮件等）。

架构图如下：
```
请求 → 拦截器 → 检测@CaptchaAuth → 提取Header(key/category/code) 
    → 工厂获取服务 → 调用verify(key, code) → 通过则放行，否则抛异常
```

## 3. 设计原则
- **单一职责**：拦截器只负责认证流程的编排，具体验证逻辑委托给`CaptchaService`，符合SRP。
- **开闭原则**：通过工厂模式，新增验证码类型无需修改拦截器，只需实现`CaptchaService`并注册到工厂。
- **无侵入性**：业务代码只需添加注解，无需感知验证细节，便于维护和测试。
- **配置灵活**：注解支持动态指定参数名称，默认值与全局常量保持一致，也可按需覆盖。

## 4. 与Spring生态的融合
- 拦截器注册到`WebMvcConfigurer`中，可与Spring Security等框架协同工作。
- 利用`HandlerInterceptor`的`preHandle`，在Controller执行前进行验证，失败时可直接返回错误响应（通过全局异常处理器）。
- 使用`WebUtils.getHandlerMethodAnnotation`（自定义工具）便捷获取方法或类级别的注解，支持注解继承（若`@Inherited`）。

## 5. 异常处理策略
验证失败时抛出`CaptchaErrorCode`异常（含错误码和消息），由全局异常处理器统一转换为HTTP响应，保证响应格式一致。错误码涵盖：
- `CAPTCHA_PARAMETERS_INCOMPLETE`：参数缺失
- `CAPTCHA_VERIFY_FAILURE`：验证失败（如过期或错误）

## 6. 适用场景
- 登录/注册接口
- 敏感操作（修改密码、支付确认）
- 高频请求限流前的二次验证
