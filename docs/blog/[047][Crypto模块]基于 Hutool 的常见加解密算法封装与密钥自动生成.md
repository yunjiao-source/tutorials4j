# [047][Crypto模块]基于 Hutool 的常见加解密算法封装与密钥自动生成

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

Hutool 是一个优秀的 Java 工具库，提供了简单易用的加密 API。但直接在业务代码中调用 Hutool 仍然存在耦合。本文展示如何将 Hutool 的各种加解密算法封装为统一的 `CryptoProcessor` 接口，并实现密钥自动生成能力。

## 一、为什么选择 Hutool？

Hutool 的加密模块（`cn.hutool.crypto`）封装了 JDK 内置的加密算法和 BouncyCastle 的国密算法，使用起来非常简洁：

```java
// 对称加密示例
AES aes = SecureUtil.aes(keyBytes);
String encryptHex = aes.encryptHex(data);
String decryptStr = aes.decryptStr(encryptHex);
```

相比于直接操作 `Cipher`，Hutool 大大减少了样板代码。同时，它支持 SM2/SM3/SM4 等国密算法，满足合规要求。

## 二、封装对称加密处理器：以 AES 为例

实现 `CryptoProcessor` 接口，内部持有 Hutool 的 `AES` 对象：

```java
@RequiredArgsConstructor
public class AESCryptoProcessor implements CryptoProcessor {
    protected final AES aes;
    protected final SecretKey secretKey;

    public static AESCryptoProcessor create() {
        return create(SecretKeyGenerator.generateASEKey());
    }

    public static AESCryptoProcessor create(SecretKey secretKey) {
        Assert.notNull(secretKey, "'secretKey' must not be null");
        AES aes = SecureUtil.aes(secretKey.symmetricKeyByte());
        return new AESCryptoProcessor(aes, secretKey);
    }

    @Override
    public String encrypt(String data) {
        return aes.encryptHex(data);
    }

    @Override
    public String decrypt(String data) {
        return aes.decryptStr(data);
    }

    @Override
    public CryptoCategory getCategory() {
        return CryptoCategory.AES;
    }
    // 其他方法...
}
```

`SecretKey` 是一个简单的值对象，封装了对称密钥的 Hex 字符串（对于非对称，包含公私钥对）。`SecretKeyGenerator` 负责生成各种算法的密钥。

## 三、密钥自动生成器

`SecretKeyGenerator` 是一个接口，但提供了静态方法：

```java
public interface SecretKeyGenerator {
    static SecretKey generateASEKey() {
        byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        return new SecretKey(HexUtil.encodeHexStr(encoded));
    }

    static SecretKey generateRSAKey() {
        KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), 1024);
        return new SecretKey(
            HexUtil.encodeHexStr(keyPair.getPublic().getEncoded()),
            HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded()));
    }

    static SecretKey generateSM2Key() { ... }
    static SecretKey generateHmacSHA256Key() { ... }
    // ...
}
```

这样，当用户在配置文件中没有提供密钥时，处理器可以通过 `create()` 方法自动生成密钥并创建实例，实现了“开箱即用”。

## 四、封装非对称加密处理器：以 RSA 为例

非对称加密相比对称加密多了一对密钥。Hutool 的 `RSA` 类支持通过公钥加密、私钥解密：

```java
@RequiredArgsConstructor
public class RSACryptoProcessor implements CryptoProcessor {
    private final RSA rsa;
    private final SecretKey secretKey;

    public static RSACryptoProcessor create(SecretKey secretKey) {
        RSA rsa = SecureUtil.rsa(secretKey.privateKeyByte(), secretKey.publicKeyByte());
        return new RSACryptoProcessor(rsa, secretKey);
    }

    @Override
    public String encrypt(String content) {
        return rsa.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
    }

    @Override
    public String decrypt(String content) {
        return rsa.decryptStr(content, KeyType.PrivateKey);
    }
}
```

注意：非对称加密对数据长度有限制，实际生产环境中通常会组合对称加密（如用 RSA 加密对称密钥，再用对称密钥加密业务数据）。本框架为简化演示，直接使用非对称加密短文本。

## 五、封装 HMAC 摘要处理器

摘要（消息认证码）接口是 `DigestProcessor`，HMac 需要密钥：

```java
@RequiredArgsConstructor
public class HmacSHA256DigestProcessor implements DigestProcessor {
    private final HMac mac;
    private final SecretKey secretKey;

    @Override
    public String digest(String content) {
        return mac.digestHex(content);
    }
}
```

无密钥的摘要（如 SHA256、SM3）则不需要 SecretKey：

```java
public class SHA256DigestProcessor implements DigestProcessor {
    private final Digester sha256;

    @Override
    public String digest(String content) {
        return sha256.digestHex(content);
    }

    @Override
    public SecretKey getSecretKey() {
        return null; // 不支持
    }
}
```

## 六、newInstance 方法的设计

每个处理器都提供了 `newInstance()` 和 `newInstance(SecretKey secretKey)` 方法，用于动态创建新的处理器实例。这在多租户场景或需要临时更换密钥时非常有用：

```java
// 创建一个默认密钥的 AES 处理器
CryptoProcessor defaultAes = AESCryptoProcessor.create();
// 根据特定租户的密钥创建新实例
CryptoProcessor tenantAes = defaultAes.newInstance(tenantSecretKey);
String encrypted = tenantAes.encrypt(data);
```

由于摘要处理器（SHA256/SM3）不需要密钥，`newInstance(SecretKey)` 返回 `null` 或抛出异常即可。

## 七、总结

通过封装 Hutool，我们获得了：

- **统一接口**：无论是对称、非对称还是摘要，都通过一致的 `encrypt/decrypt` 或 `digest` 方法调用。
- **密钥灵活性**：可以从配置文件注入 Hex 字符串，也可以自动生成，满足开发和生产不同阶段的需求。
- **易于测试**：可以轻松 mock `CryptoProcessor`，无需真实调用加密库。
- **支持国密**：SM2/SM3/SM4 同样被封装在框架中，符合安全合规要求。

下一步可以扩展的包括：密钥版本管理、密钥轮换、与 KeyStore 集成等。