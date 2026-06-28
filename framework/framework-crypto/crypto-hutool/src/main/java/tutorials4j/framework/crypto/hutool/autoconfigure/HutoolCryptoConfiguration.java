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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HutoolCryptoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO-HUTOOL] Hutool Crypto Configuration");
  }

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

  @Bean
  @ConditionalOnMissingBean
  SHA256DigestProcessor sha256CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] SHA256 Digest Processor");
    return SHA256DigestProcessor.create(
        properties.getSalt(), properties.getSaltPosition(), properties.getDigestCount());
  }

  @Bean
  @ConditionalOnMissingBean
  SM3DigestProcessor sm3CryptoProcessor(CryptoProperties properties) {
    log.trace("[CRYPTO-HUTOOL] SM3 Digest Processor");
    return SM3DigestProcessor.create(
        properties.getSalt(), properties.getSaltPosition(), properties.getDigestCount());
  }
}
