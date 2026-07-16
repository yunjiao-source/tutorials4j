# [045][Crypto模块]设计一个可扩展的加解密框架：策略模式与工厂模式实战

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在企业级应用中，数据加解密是一个常见需求。然而，加密算法种类繁多（AES、RSA、SM2、SM4 等），且不同场景可能需要更换算法。如果每次都在业务代码中直接调用具体算法，会带来强耦合、难以维护等问题。本文通过一个基于 Spring Boot 的加解密框架设计，介绍如何利用**策略模式**与**工厂模式**构建一个可插拔、易扩展的加解密框架。

## 一、为什么要设计统一的加解密接口？

看一段常见的“坏味道”代码：

```java
// 业务代码中直接依赖具体算法
String encrypted = AESUtil.encrypt(data, key);
String decrypted = AESUtil.decrypt(encrypted, key);
```

当需要从 AES 切换到 SM4，或者从 RSA 切换到 SM2 时，不得不修改所有调用的地方。更糟糕的是，不同算法需要的密钥格式、参数各不相同，业务代码会变得臃肿。

理想的框架应该：
- 提供统一的加解密接口，业务代码只依赖接口。
- 允许通过配置或注解动态切换算法。
- 支持对称加密、非对称加密、摘要（哈希）等不同类别。
- 易于扩展新算法。

## 二、核心接口设计：策略模式

首先定义所有加解密处理器都要实现的接口 `CryptoProcessor`：

```java
public interface CryptoProcessor {
    CryptoCategory getCategory();          // 返回算法类别，如 AES、RSA
    SecretKey getSecretKey();              // 获取当前使用的密钥信息
    CryptoProcessor newInstance();         // 创建新的处理器实例（自动生成密钥）
    CryptoProcessor newInstance(SecretKey secretKey); // 指定密钥创建实例
    String decrypt(String content);
    String encrypt(String content);
}
```

这里使用了**策略模式**：每种算法（AES、SM2、RSA 等）都是一个具体的策略实现类，它们都遵循相同的接口。业务代码只需要持有 `CryptoProcessor` 引用，调用 `encrypt/decrypt` 即可，无需关心底层是哪种算法。

同理，摘要类接口单独定义：

```java
public interface DigestProcessor {
    DigestCategory getCategory();
    SecretKey getSecretKey();
    DigestProcessor newInstance();
    DigestProcessor newInstance(SecretKey secretKey);
    String digest(String content);
    String digest(String content, Charset charset);
}
```

通过接口分离，加解密和摘要两类处理器可以分别管理。

## 三、工厂模式：统一查找与获取

有了多种策略实现，如何根据算法名称或类别快速获取对应的处理器？答案是**工厂模式**。框架中设计了 `CryptoProcessorFactory`：

```java
public class CryptoProcessorFactory {
    public static final CryptoProcessorFactory instance = new CryptoProcessorFactory();
    protected EnumMap<CryptoCategory, CryptoProcessor> processors = new EnumMap<>(CryptoCategory.class);

    public CryptoProcessor findProcessor(CryptoCategory category) {
        CryptoProcessor processor = processors.get(category);
        if (processor == null) {
            throw new CryptoException("根据分类查找加解密处理器未找到, 分类是：" + category);
        }
        return processor;
    }

    public void setProcessors(Map<CryptoCategory, CryptoProcessor> processors) {
        this.processors.putAll(processors);
    }
}
```

`EnumMap` 保证了分类与处理器之间的高效映射。工厂以单例形式存在，方便在框架各处获取。

## 四、Spring 集成：自动注册策略实现

策略类和工厂都准备好了，如何让 Spring 容器自动将所有策略实现注册到工厂中？利用 Spring 的 `ObjectProvider` 和自动配置：

```java
@Configuration
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    CryptoProcessorFactory cryptoProcessorFactory(ObjectProvider<CryptoProcessor> providers) {
        Map<CryptoCategory, CryptoProcessor> processors =
            providers.stream().collect(Collectors.toMap(CryptoProcessor::getCategory, m -> m));
        CryptoProcessorFactory.instance.setProcessors(processors);
        return CryptoProcessorFactory.instance;
    }
}
```

- `ObjectProvider<CryptoProcessor>` 会获取容器中所有 `CryptoProcessor` 类型的 Bean（即各个算法的实现类）。
- 通过 `Collectors.toMap` 按照 `getCategory()` 分类建立映射。
- 注入到工厂实例中。

这样做的好处：新增一种算法时，只需编写一个实现 `CryptoProcessor` 的新类，并把它声明为 Spring Bean，工厂就会自动识别，完全符合**开闭原则**。

## 五、枚举与类别的关联

为了统一管理支持的算法类型，定义 `CryptoCategory` 枚举：

```java
public enum CryptoCategory {
    AES, DES, SM2, SM4, RSA;
}
```

每个策略实现类必须返回其中一个类别。例如 `AESCryptoProcessor` 实现 `getCategory()` 返回 `CryptoCategory.AES`。

非对称加密还有一个额外的枚举 `AsymmetricCryptoStrategy`，用于配置文件中选择默认的非对称算法（RSA 或 SM2）：

```java
public enum AsymmetricCryptoStrategy {
    STANDARD(CryptoCategory.RSA),
    SM(CryptoCategory.SM2);
    private final CryptoCategory category;
}
```

## 六、代码示例：如何新增一个国密 SM9 处理器

假设需要新增 SM9 算法（国产标识加密），步骤如下：

1. 在 `CryptoCategory` 中增加 `SM9`。
2. 实现 `CryptoProcessor` 接口：

```java
public class SM9CryptoProcessor implements CryptoProcessor {
    private final SM9 sm9;
    private final SecretKey secretKey;

    public static SM9CryptoProcessor create() { ... }
    public static SM9CryptoProcessor create(SecretKey secretKey) { ... }

    @Override public CryptoCategory getCategory() { return CryptoCategory.SM9; }
    @Override public String encrypt(String content) { ... }
    @Override public String decrypt(String content) { ... }
    // 其他方法...
}
```

3. 将该类声明为 Spring Bean（例如通过 `@Bean` 或 `@Component`）。框架会自动检测并将其注册到 `CryptoProcessorFactory` 中。

业务代码中获取 SM9 处理器：

```java
CryptoProcessor processor = CryptoProcessorFactory.instance.findProcessor(CryptoCategory.SM9);
String encrypted = processor.encrypt("hello");
```

## 七、总结

通过策略模式 + 工厂模式 + Spring 自动注册，我们构建了一个高内聚低耦合的加解密框架。核心优势：

- **可扩展**：新增算法无需修改任何现有代码。
- **可配置**：通过 `@ConfigurationProperties` 可以灵活指定密钥、盐值等。
- **统一接口**：业务代码不再依赖具体加密库（如 Hutool、BouncyCastle），便于后续切换底层实现。
- **类型安全**：使用枚举而非字符串表示算法类别，避免拼写错误。

这种设计思想同样适用于其它中间件集成（如序列化、日志、缓存等），值得在大型项目中推广。
