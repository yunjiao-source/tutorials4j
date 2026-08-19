package tutorials4j.framework.crypto.hutool.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;
import tutorials4j.framework.crypto.hutool.processor.AESCryptoProcessor;
import tutorials4j.framework.crypto.hutool.processor.DESCryptoProcessor;
import tutorials4j.framework.crypto.hutool.processor.HmacSHA256DigestProcessor;
import tutorials4j.framework.crypto.hutool.processor.HmacSHA512DigestProcessor;
import tutorials4j.framework.crypto.hutool.processor.RSACryptoProcessor;
import tutorials4j.framework.crypto.hutool.processor.SHA256DigestProcessor;
import tutorials4j.framework.crypto.hutool.processor.SM2CryptoProcessor;
import tutorials4j.framework.crypto.hutool.processor.SM3DigestProcessor;
import tutorials4j.framework.crypto.hutool.processor.SM4CryptoProcessor;

/**
 * Hutool 加密自动配置类，负责创建并注册基于 Hutool 实现的各类加密与摘要处理器 Bean。
 *
 * <p>根据 {@link CryptoProperties} 中配置的密钥信息（对称密钥、公钥/私钥、盐值与摘要参数等）创建对应的处理器； 未配置密钥时各处理器自动生成随机密钥。所有 Bean
 * 均标注 {@code @ConditionalOnMissingBean}， 允许使用方通过自定义 Bean 覆盖默认实现。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HutoolCryptoConfiguration {
  /** 初始化完成后输出一条 trace 日志，用于确认配置类已加载。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO-HUTOOL] Hutool Crypto Configuration");
  }

  /**
   * 创建 AES 加密处理器 Bean，未配置密钥时自动生成随机密钥。
   *
   * @param properties 加密配置属性
   * @return AES 加密处理器
   */
  @Bean
  @ConditionalOnMissingBean
  AESCryptoProcessor aesCryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] AES Crypto Processor");
    String secretKeyHex = properties.getSecretKeyHex();
    if (StringUtils.isBlank(secretKeyHex)) {
      return AESCryptoProcessor.create();
    } else {
      return AESCryptoProcessor.create(new SecretKey(secretKeyHex));
    }
  }

  /**
   * 创建 DES 加密处理器 Bean，未配置密钥时自动生成随机密钥。
   *
   * @param properties 加密配置属性
   * @return DES 加密处理器
   */
  @Bean
  @ConditionalOnMissingBean
  DESCryptoProcessor DESCryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] DES Crypto Processor");
    String secretKeyHex = properties.getSecretKeyHex();
    if (StringUtils.isBlank(secretKeyHex)) {
      return DESCryptoProcessor.create();
    } else {
      return DESCryptoProcessor.create(new SecretKey(secretKeyHex));
    }
  }

  /**
   * 创建 SM2 国密非对称加密处理器 Bean，未配置公钥/私钥时自动生成随机密钥对。
   *
   * @param properties 加密配置属性
   * @return SM2 加密处理器
   */
  @Bean
  @ConditionalOnMissingBean
  SM2CryptoProcessor sm2CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] SM2 Crypto Processor");
    String publicKeyHex = properties.getPublicKeyHex();
    String privateKeyHex = properties.getPrivateKeyHex();
    if (StringUtils.isAnyBlank(publicKeyHex, privateKeyHex)) {
      return SM2CryptoProcessor.create();
    } else {
      return SM2CryptoProcessor.create(new SecretKey(publicKeyHex, privateKeyHex));
    }
  }

  /**
   * 创建 SM4 国密对称加密处理器 Bean，未配置密钥时自动生成随机密钥。
   *
   * @param properties 加密配置属性
   * @return SM4 加密处理器
   */
  @Bean
  @ConditionalOnMissingBean
  SM4CryptoProcessor sm4CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] SM4 Crypto Processor");
    String secretKeyHex = properties.getSecretKeyHex();
    if (StringUtils.isBlank(secretKeyHex)) {
      return SM4CryptoProcessor.create();
    } else {
      return SM4CryptoProcessor.create(new SecretKey(secretKeyHex));
    }
  }

  /**
   * 创建 HmacSHA256 摘要处理器 Bean，未配置密钥时自动生成随机密钥。
   *
   * @param properties 加密配置属性
   * @return HmacSHA256 摘要处理器
   */
  @Bean
  @ConditionalOnMissingBean
  HmacSHA256DigestProcessor hmacSHA256CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] HmacSHA256 Digest Processor");
    String secretKeyHex = properties.getSecretKeyHex();
    if (StringUtils.isBlank(secretKeyHex)) {
      return HmacSHA256DigestProcessor.create();
    } else {
      return HmacSHA256DigestProcessor.create(new SecretKey(secretKeyHex));
    }
  }

  /**
   * 创建 HmacSHA512 摘要处理器 Bean，未配置密钥时自动生成随机密钥。
   *
   * @param properties 加密配置属性
   * @return HmacSHA512 摘要处理器
   */
  @Bean
  @ConditionalOnMissingBean
  HmacSHA512DigestProcessor hmacSHA512CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] HmacSHA512 Digest Processor");
    String secretKeyHex = properties.getSecretKeyHex();
    if (StringUtils.isBlank(secretKeyHex)) {
      return HmacSHA512DigestProcessor.create();
    } else {
      return HmacSHA512DigestProcessor.create(new SecretKey(secretKeyHex));
    }
  }

  /**
   * 创建 RSA 非对称加密处理器 Bean，未配置公钥/私钥时自动生成随机密钥对。
   *
   * @param properties 加密配置属性
   * @return RSA 加密处理器
   */
  @Bean
  @ConditionalOnMissingBean
  RSACryptoProcessor rsaCryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] AES Digest Processor");
    String publicKeyHex = properties.getPublicKeyHex();
    String privateKeyHex = properties.getPrivateKeyHex();
    if (StringUtils.isAnyBlank(publicKeyHex, privateKeyHex)) {
      return RSACryptoProcessor.create();
    } else {
      return RSACryptoProcessor.create(new SecretKey(publicKeyHex, privateKeyHex));
    }
  }

  /**
   * 创建 SHA256 摘要处理器 Bean，使用配置的盐值与摘要参数。
   *
   * @param properties 加密配置属性
   * @return SHA256 摘要处理器
   */
  @Bean
  @ConditionalOnMissingBean
  SHA256DigestProcessor sha256CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] SHA256 Digest Processor");
    return SHA256DigestProcessor.create(
        properties.getSalt(), properties.getSaltPosition(), properties.getDigestCount());
  }

  /**
   * 创建 SM3 国密摘要处理器 Bean，使用配置的盐值与摘要参数。
   *
   * @param properties 加密配置属性
   * @return SM3 摘要处理器
   */
  @Bean
  @ConditionalOnMissingBean
  SM3DigestProcessor sm3CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] SM3 Digest Processor");
    return SM3DigestProcessor.create(
        properties.getSalt(), properties.getSaltPosition(), properties.getDigestCount());
  }
}
