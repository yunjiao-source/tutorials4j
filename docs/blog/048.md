# [048][Crypto模块]Spring Boot 请求体自动解密：@Crypto 注解 + RequestBodyAdvice 实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在 Web 应用中，前端经常需要对请求体进行加密后传输，后端在进入 Controller 之前自动解密。Spring MVC 提供了 `RequestBodyAdvice` 接口，允许我们在请求体绑定到方法参数之前对 `HttpInputMessage` 进行拦截和修改。本文结合自定义注解 `@Crypto`，实现优雅的自动解密。

## 一、需求场景

假设前端使用 RSA 公钥加密了 JSON 请求体，后端收到的是一个密文字符串：

```
POST /api/user
Content-Type: text/plain

"encrypted_base64_string..."
```

我们希望 Controller 方法直接接收到解密后的 Java 对象：

```java
@PostMapping("/user")
@Crypto(request = true)
public UserDto createUser(@RequestBody UserDto userDto) {
    // userDto 已经是解密后的对象
}
```

## 二、自定义注解 @Crypto

注解标记在方法上，用于声明该方法是否需要请求体解密以及响应加密（响应加密可后续扩展）：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Crypto {
    boolean response() default false;
    boolean request() default true;
}
```

## 三、实现 RequestBodyAdvice

`CryptoRequestBodyAdvice` 实现 `RequestBodyAdvice` 接口，并重写关键方法：

```java
@RestControllerAdvice
@RequiredArgsConstructor
public class CryptoRequestBodyAdvice implements RequestBodyAdvice {
    private final CryptoProcessor cryptoProcessor; // 非对称加密处理器（如 RSA 或 SM2）

    @Override
    public boolean supports(MethodParameter parameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Crypto crypto = parameter.getMethodAnnotation(Crypto.class);
        return crypto != null && crypto.request();
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage,
                                           MethodParameter parameter,
                                           Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException {
        // 1. 读取原始加密字符串
        String encrypted = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(encrypted)) {
            return inputMessage;
        }
        // 2. 去除可能的前后引号（前端传 JSON 字符串时可能带引号）
        encrypted = encrypted.replaceAll("^\"|\"$", "");
        // 3. 解密
        String decrypted = cryptoProcessor.decrypt(encrypted);
        // 4. 包装成新的 HttpInputMessage
        return new DecryptHttpInputMessage(inputMessage, decrypted.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body; // 直接返回
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage,
                                  MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    // 内部类用于替换请求体
    public static class DecryptHttpInputMessage implements HttpInputMessage {
        private final HttpInputMessage original;
        private final byte[] data;

        public DecryptHttpInputMessage(HttpInputMessage original, byte[] data) {
            this.original = original;
            this.data = data;
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(data);
        }

        @Override
        public HttpHeaders getHeaders() {
            return original.getHeaders();
        }
    }
}
```

## 四、获取公钥的端点

前端需要公钥来进行加密，因此提供一个公开的 GET 接口返回公钥 Hex 字符串：

```java
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoEndpoint {
    private final CryptoProperties properties;

    @GetMapping("publicKey")
    public String getPublicKey() {
        CryptoProcessor processor = CryptoProcessorFactory.instance.findProcessor(
            properties.getAsymmetricCryptoStrategy().getCategory());
        return processor.getSecretKey().publicKeyHex();
    }
}
```

前端示例流程：
1. 先调用 `/api/crypto/publicKey` 获取公钥。
2. 使用公钥加密请求体数据（如 JSON 字符串）。
3. 在 POST 请求的 body 中发送密文，并确保 `Content-Type` 为 `text/plain` 或能被读取为字符串即可。
4. 后端自动解密，Controller 正常接收 Java 对象。

## 五、注意事项与最佳实践

1. **Content-Type 的处理**：本实现假设请求体是纯文本（`text/plain`）或者 Spring 能将其读取为字符串。如果你希望仍然使用 `application/json` 但 body 是密文字符串，可能会遇到类型转换问题。一个常见方案是自定义一个 `@EncryptedRequestBody` 参数解析器，或者约定前端将密文放在 JSON 对象的某个字段中。

2. **性能考虑**：解密操作可能较慢（尤其非对称加密），建议只在必要的接口上使用 `@Crypto`，并通过 `supports` 方法过滤。

3. **错误处理**：解密失败时应抛出异常，由全局异常处理器返回友好提示。

4. **响应加密**：类似地可以实现 `ResponseBodyAdvice` 对返回值进行加密，本文未展示，但框架的 `@Crypto(response = true)` 预留了扩展点。

5. **日志记录**：在 `beforeBodyRead` 中打印 debug 日志，方便排查问题，但注意**不要**打印明文敏感数据。

## 六、总结

通过 `RequestBodyAdvice` + 自定义注解，我们实现了近乎透明的请求体自动解密能力，具有以下优点：

- **无侵入**：Controller 代码完全不需要关心加密逻辑，保持纯净的业务代码。
- **可配置**：通过 `@Crypto(request = true)` 灵活开关。
- **算法无关**：解密处理器 `CryptoProcessor` 可以根据配置选用 RSA、SM2 等算法，前端获取对应的公钥即可。
- **易于测试**：单元测试时可以关闭解密功能，直接发送明文。

这种模式非常适合需要接口加密传输的内部系统或暴露到公网的 API 网关，能够在不影响业务开发的前提下提升安全性。