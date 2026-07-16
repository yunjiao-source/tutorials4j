# [046][Crypto模块]Spring Boot 自动配置进阶：按需装配加解密处理器

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

Spring Boot 的自动配置（Auto-Configuration）极大地简化了组件集成。本文以一个加解密框架为例，深入讲解如何利用 `@ConditionalOnMissingBean`、`@EnableConfigurationProperties` 以及多个配置类的拆分，实现灵活的按需装配。

## 一、配置属性类：CryptoProperties

首先定义框架需要的配置项，使用 `@ConfigurationProperties` 绑定 `application.yml` 中的前缀：

```java
@Data
@ConfigurationProperties(prefix = "tutorials4j.crypto")
public class CryptoProperties {
    private AsymmetricCryptoStrategy asymmetricCryptoStrategy = AsymmetricCryptoStrategy.STANDARD;
    private String secretKeyHex;    // 对称密钥
    private String privateKeyHex;   // 非对称私钥
    private String publicKeyHex;    // 非对称公钥
    private String salt;            // 摘要盐值
    private int saltPosition = 0;
    private int digestCount = 1;
}
```

用户可在 `application.yml` 中配置：

```yaml
tutorials4j:
  crypto:
    asymmetric-crypto-strategy: SM   # 使用 SM2
    secret-key-hex: "abcd1234..."
    salt: "my-salt"
```

## 二、核心自动配置类：CryptoConfiguration

这个类负责将底层的工厂 Bean 装配到 Spring 容器中：

```java
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    CryptoProcessorFactory cryptoProcessorFactory(ObjectProvider<CryptoProcessor> providers) {
        // 收集所有 CryptoProcessor 并注册到工厂
    }

    @Bean
    @ConditionalOnMissingBean
    DigestProcessorFactory digestProcessorFactory(ObjectProvider<DigestProcessor> providers) {
        // 收集所有 DigestProcessor
    }
}
```

`@ConditionalOnMissingBean` 是关键：如果用户已经自定义了工厂 Bean，则不会重复创建，允许覆盖默认行为。

## 三、Hutool 实现自动配置：HutoolCryptoConfiguration

具体的加密算法实现（基于 Hutool）在另一个配置类中，按需创建各个处理器的 Bean：

```java
@Configuration(proxyBeanMethods = false)
public class HutoolCryptoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AESCryptoProcessor aesCryptoProcessor(CryptoProperties properties) {
        if (StringUtils.isBlank(properties.getSecretKeyHex())) {
            return AESCryptoProcessor.create(); // 自动生成密钥
        } else {
            return AESCryptoProcessor.create(new SecretKey(properties.getSecretKeyHex()));
        }
    }

    @Bean
    @ConditionalOnMissingBean
    SM2CryptoProcessor sm2CryptoProcessor(CryptoProperties properties) {
        // 从配置中读取公钥私钥或自动生成
    }
    // 其他处理器类似...
}
```

这里每个 `@Bean` 都使用了 `@ConditionalOnMissingBean`，允许用户覆盖特定处理器的实现。例如，如果用户想自定义一个增强版的 AES 处理器，只需在项目中声明一个 `AESCryptoProcessor` 类型的 `@Bean`，框架自动配置就会跳过默认创建。

## 四、Web 功能自动配置：CryptoWebConfiguration

如果项目中需要请求体自动解密功能，再添加一个独立的配置类：

```java
@Configuration(proxyBeanMethods = false)
public class CryptoWebConfiguration {
    @Bean
    @ConditionalOnMissingBean
    CryptoRequestBodyAdvice cryptoRequestBodyAdvice(CryptoProperties properties) {
        CryptoProcessor processor = CryptoProcessorFactory.instance.findProcessor(
            properties.getAsymmetricCryptoStrategy().getCategory());
        return new CryptoRequestBodyAdvice(processor);
    }

    @Bean
    @ConditionalOnMissingBean
    CryptoEndpoint cryptoEndpoint(CryptoProperties properties) {
        return new CryptoEndpoint(properties);
    }
}
```

这样做的好处是：如果用户不需要 Web 功能（例如纯后端服务），可以不引入 `crypto-web` 模块，自动配置就不会生效，避免了不必要的 Bean 创建。

## 五、条件装配的进阶用法

除了 `@ConditionalOnMissingBean`，Spring Boot 还提供了很多条件注解，框架未来可以进一步扩展：

- `@ConditionalOnProperty`：只有配置了某个属性才创建 Bean。例如可设置 `tutorials4j.crypto.enabled=true` 来全局开关。
- `@ConditionalOnClass`：只在类路径存在某个类时才装配。例如检测到 Hutool 的类，才创建 Hutool 处理器。
- `@ConditionalOnWebApplication`：只在 Web 环境下装配 `CryptoWebConfiguration`。

## 六、日志输出与调试

在每个配置类中使用 `@PostConstruct` 打印日志，方便用户确认配置是否生效：

```java
@PostConstruct
public void postConstruct() {
    log.debug("[CRYPTO-CORE] Crypto Configuration");
}
```

用户开启 `debug` 日志级别后，可以看到框架装配了哪些处理器，密钥是否自动生成等关键信息。

## 七、总结

通过合理的拆分和条件化配置，Spring Boot Starter 级别的框架可以达到：

- **按需加载**：用户引入 `crypto-hutool` 依赖才激活 Hutool 实现；引入 `crypto-web` 才激活 Web 解密功能。
- **易于覆盖**：`@ConditionalOnMissingBean` 给用户最大的定制空间。
- **配置集中**：所有配置项通过 `@ConfigurationProperties` 统一管理，支持 `yaml` 自动提示。
- **低入侵**：框架不会强制用户使用特定的配置方式，默认值保证了开箱即用。

这种设计模式是开发企业内部通用 starter 的最佳实践，值得参考。