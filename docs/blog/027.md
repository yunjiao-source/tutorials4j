# [027][Web模块]基于 Spring MVC 的 API 签名校验拦截器设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j

在微服务架构中，API 的安全性至关重要。为了防止请求被篡改、重放攻击以及未授权访问，通常需要为接口增加签名验证机制。本文介绍一套基于 Spring MVC 的通用签名拦截器实现，涵盖签名生成、验证、防重放、时间窗校验等核心功能，并提供可扩展的密钥仓库与缓存抽象。

## 1. 背景与需求

开放 API 或内部服务之间的 HTTP 调用，常面临以下安全威胁：

- **数据篡改**：请求参数在网络传输中被恶意修改。
- **重放攻击**：窃取合法请求后反复发送，造成系统状态异常。
- **身份伪造**：攻击者使用非法 `appKey` 冒充合法应用。

传统的解决方案是在请求头中加入签名（Signature），客户端使用约定算法对请求关键信息（方法、路径、参数、时间戳、随机数等）计算签名，服务端以相同规则校验。同时配合时间窗口和 `nonce`（一次性的随机字符串）来防御重放。

本文分析并实现的签名拦截器正是满足上述需求的一个完整模块。

## 2. 整体架构

该签名模块由以下几个核心组件构成：

| 组件 | 职责 |
|------|------|
| `SignatureHandlerInterceptor` | Spring MVC 拦截器，在 `preHandle` 中执行签名校验逻辑 |
| `SignatureUtils` | 签名工具类，提供签名生成和验证的算法（HmacSHA256） |
| `SignatureKeyRepository` | 密钥仓库接口，根据 `appKey` 获取 `appSecret` |
| `SignatureCacheTemplate` | 防重放缓存模板，存储已使用过的 `nonce`（基于 Redis） |
| `RequiredSignature` 注解 | 标注需要签名校验的接口方法，并配置时间窗、是否检查 `nonce` 等属性 |

整体调用流程如下图所示：

```
Request → SignatureHandlerInterceptor.preHandle()
    ├─ 检查方法是否有 @RequiredSignature
    ├─ 提取 header 中的 appKey、timestamp、nonce、signature
    ├─ 参数完整性校验
    ├─ 时间窗有效期校验
    ├─ Nonce 防重放校验（如果启用）
    ├─ 从 SignatureKeyRepository 获取 appSecret
    ├─ 读取 request body
    ├─ 调用 SignatureUtils.verify 进行签名比对
    ├─ 校验成功 → 记录 nonce（如果启用） → 放行
    └─ 校验失败 → 抛出 SignatureException → 统一异常处理返回错误响应
```

## 3. 核心组件详解

### 3.1 `@RequiredSignature` 注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredSignature {
    boolean required() default true;
    long timeWindow() default 300;      // 时间窗，单位秒
    boolean checkNonce() default true;  // 是否校验 nonce
}
```

- `required`：是否强制校验签名，默认为 `true`。可用于动态开关某个接口的签名要求。
- `timeWindow`：允许的时间偏差，单位秒。防止因服务器时钟不同步导致合法请求被误判过期。
- `checkNonce`：是否启用防重放校验。启用时，每次请求的 `nonce` 必须唯一且仅在时间窗内有效。

### 3.2 `SignatureHandlerInterceptor` 拦截器

#### 核心校验步骤

1. **获取方法上的注解**：若方法未标注 `@RequiredSignature` 或 `required=false`，则直接放行。
2. **从请求头提取签名参数**：约定使用如下 header 字段：
   - `X-Signature-AppKey`
   - `X-Signature-Timestamp`
   - `X-Signature-Nonce`
   - `X-Signature`
3. **参数完整性检查**：任一字段为空则抛出 `SignatureException("签名参数不完整")`。
4. **时间戳验证**：`Math.abs(currentTime - requestTime) > timeWindow * 1000` 则认为过期。
5. **Nonce 防重放**：若 `checkNonce` 为 `true`，则查询 `SignatureCacheTemplate` 中是否已存在该 `nonce`。若存在，说明是重复请求，抛出异常。
6. **获取应用密钥**：通过 `SignatureKeyRepository.getSecretKey(appKey)` 获取 `appSecret`。若不存在则抛出“未找到签名KEY”。
7. **读取请求体**：调用 `getRequestBody()` 方法读取 HTTP 请求的 body 内容（注意需要处理流不可重复读的问题，实际生产环境常搭配 `ContentCachingRequestWrapper` 使用）。
8. **验签**：调用 `SignatureUtils.verify()` 比对服务端计算的签名与客户端传入的签名是否一致。
9. **记录 nonce**：校验通过后，将本次 `nonce` 存入 Redis 缓存，过期时间通常设置为 `timeWindow`，从而在时间窗内防止重放。

#### 请求体读取的注意事项

示例代码中使用 `IOUtils.toByteArray(request.getReader())` 直接读取了 `HttpServletRequest` 的输入流。这种方式会导致后续 Controller 无法再次获取请求体（流已被消费）。实际落地时应使用 `ContentCachingRequestWrapper` 对原始 request 进行包装，或者采用 `@RequestBody` 配合 `InputStream` 可重复读取的装饰器模式。

### 3.3 `SignatureUtils` 签名算法

签名算法采用业界通用的 **HmacSHA256**，参数字典序排序后拼接为字符串，再计算 HMAC。

#### 待签名字符串构造规则

所有参与签名的参数放入一个 `TreeMap` 中（自动按 key 字典序排序），格式为：

```
appKey=xxx&timestamp=xxx&nonce=xxx&method=POST&path=/api/user&body={"name":"test"}
```

- `body` 字段仅在非空时加入，避免空 body 破坏签名。
- `method` 和 `path` 是 HTTP 请求的核心元数据，防止请求方法或路径被篡改。

#### 签名生成与验证

```java
String message = buildSignMessage(...);
String signature = hmacSha256(appSecret, message);
```

HmacSHA256 输出字节数组，再转为十六进制字符串。客户端必须以完全相同的方式生成签名。

#### 优势

- 密钥 `appSecret` 不参与报文传输，仅用于 HMAC 计算，安全性高。
- 参数排序保证了无论客户端参数顺序如何，都能生成一致的签名。

### 3.4 `SignatureKeyRepository` 密钥仓库

这是一个函数式接口，仅有一个方法 `String getSecretKey(String appKey)`。框架提供了基于内存 `Map` 的简单实现 `SimpleSignatureKeyRepository`，适用于测试或静态密钥场景。

生产环境建议自行实现该接口，对接数据库、配置中心或远程密钥管理服务（KMS），并增加缓存和自动刷新能力。

### 3.5 防重放缓存 `SignatureCacheTemplate`

该类继承自 `AbstractRedisCacheTemplate`，底层使用 Redis 存储已使用的 `nonce`。缓存模板抽象层封装了常见的 `put`、`get`、`exists`、`delete` 操作，并且通过 `valueGenerator` 方法为每个 `nonce` 生成一个唯一值（实际只需占位，因为仅判断 key 是否存在）。

#### 为何使用 Redis？

- `nonce` 需要在时间窗内（几十秒到几分钟）快速判断是否存在，并发量高时内存型缓存更合适。
- Redis 支持 TTL（生存时间），可以自动清理过期的 `nonce`，无需手动删除。
- 分布式环境下，多个服务实例共享同一个 Redis 集群，`nonce` 防重放可以全局生效。

`SignatureCacheTemplate` 依赖的 `AbstractRedisCacheTemplate` 通过 `CacheManagerCreatorFactory` 获取 Redis 缓存实例，这是一个典型的工厂模式与模板方法模式结合的示例。

## 4. 工作流程示例

假设客户端请求 `POST /api/order`，请求体为 `{"productId":123}`，约定的 `appKey=test-app`，`appSecret=shared-secret`。

客户端计算签名步骤：

1. 生成 `timestamp=1746720000000`（当前时间毫秒），`nonce=abc123`。
2. 构造待签名字符串：`appKey=test-app&body={"productId":123}&method=POST&nonce=abc123&path=/api/order&timestamp=1746720000000`。
3. 计算 HMAC-SHA256(shared-secret, 待签字符串) → 得到签名 `s1`。
4. 发送请求头：`X-Signature-AppKey: test-app`，`X-Signature-Timestamp: 1746720000000`，`X-Signature-Nonce: abc123`，`X-Signature: s1`。

服务端收到后，执行拦截器逻辑：

- 校验参数完整性 ✅
- 校验时间差 ≤ 300 秒 ✅
- 检查 Redis 中是否存在 `abc123` → 不存在 ✅
- 从仓库获取 `test-app` 的 secret 为 `shared-secret` ✅
- 读取请求体 `{"productId":123}`
- 用同样的算法重新计算签名，与 `s1` 比对 ✅
- 将 `abc123` 存入 Redis，TTL = 300 秒
- 放行请求，由 Controller 处理业务。

## 5. 设计亮点与可扩展性

### 5.1 无侵入式增强

通过 Spring MVC 拦截器 + 注解，业务代码完全无感知。只需在需要保护的接口上增加 `@RequiredSignature` 即可。

### 5.2 密钥仓库可插拔

`SignatureKeyRepository` 接口允许开发者自由实现密钥加载逻辑，例如：

```java
@Component
public class DatabaseSignatureKeyRepository implements SignatureKeyRepository {
    @Autowired private AppSecretMapper mapper;
    public String getSecretKey(String appKey) {
        return mapper.findByAppKey(appKey).getSecret();
    }
}
```

### 5.3 防重放的高性能设计

`nonce` 仅存储 key，值为任意唯一标识（如 UUID），不存储业务数据，减少内存占用。利用 Redis 原生 TTL 自动过期，无需后台清理任务。

### 5.4 时间窗灵活配置

`@RequiredSignature` 的 `timeWindow` 属性可针对不同接口设置不同的有效期敏感度。例如支付接口要求 30 秒内，查询接口可以放宽到 5 分钟。

## 6. 潜在问题与改进建议

### 6.1 请求体重复读取问题

如前面所述，`getRequestBody` 直接读取了原始 `HttpServletRequest` 的流，后续 Spring MVC 解析 `@RequestBody` 时会发现流已关闭，导致 `IOException`。解决方案：

- 在拦截器之前使用 `ContentCachingRequestWrapper` 包装 request，并重写 `getInputStream()` 和 `getReader()` 使其可重复读取。
- 或者在拦截器中不读取 body，而是将 body 作为签名参数的一部分另外传递（例如放在请求 body 的某个字段中），但这样会破坏签名语义。

推荐做法：在 `SignatureHandlerInterceptor` 中通过 `request.getParameterMap()` 仅对 form-urlencoded 或 query string 进行签名，而对于 JSON Body 的接口，可以使用 `@RequestBody` 绑定参数后再手动验签，或者使用包装器。

### 6.2 Nonce 存储的并发竞争

在高并发下，两个相同 `nonce` 的请求可能几乎同时到达，导致两个请求都发现 Redis 中 key 不存在，从而绕过防重放。为解决这一问题，应使用 `RedisTemplate` 的 `SET NX EX` 原子操作，或者在 `SignatureCacheTemplate` 中实现 `putIfAbsent` 并检查返回值。

当前代码中 `exists` + `create` 是两个独立操作，存在竞争窗口。建议修改为：
```java
Boolean success = redisTemplate.opsForValue().setIfAbsent(nonce, "", Duration.ofSeconds(timeWindow));
if (!success) throw new SignatureException("重复的请求");
```

### 6.3 签名参数未包含完整请求表单

对于 `application/x-www-form-urlencoded` 类型的 POST 请求，签名应当包含表单参数。但示例中只包含了 `requestBody`（对于表单格式，`getRequestBody` 返回的是 `key1=value1&key2=value2` 字符串）。一种改进是在 `buildSignMessage` 中将 query string 和 form data 也纳入排序参数，或统一要求客户端将参数放入 JSON body 中签名。

### 6.4 异常信息的泄露风险

拦截器抛出的 `SignatureException` 可能包含敏感原因（如“未找到签名KEY”），建议在统一异常处理中对外返回通用错误（如“invalid signature”），而将详细原因记录在日志中。

## 7. 总结

本文分析的签名拦截器实现简洁、模块清晰，覆盖了 API 签名校验的核心需求：身份认证、防篡改、防重放。通过合理的设计模式（模板方法、策略、工厂），提供了良好的扩展性。同时我们也指出了几个生产环境需要注意的问题，如请求体重复读取、Nonce 原子性等。

该方案适用于中小型项目的 API 安全防护，也可作为企业级网关签名功能的内置参考实现。开发者可以根据实际场景进行增强，例如增加对 query string 的支持、集成更复杂的密钥轮换机制等。
