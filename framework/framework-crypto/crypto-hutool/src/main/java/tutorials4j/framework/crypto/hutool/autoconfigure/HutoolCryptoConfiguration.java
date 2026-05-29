package tutorials4j.framework.crypto.hutool.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
    log.debug("[CRYPTO-HUTOOL] Hutool Crypto Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  AESCryptoProcessor aesCryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] AES Crypto Processor");
    return AESCryptoProcessor.create(new SecretKey(properties.getSecretKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  DESCryptoProcessor DESCryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] DES Crypto Processor");
    return DESCryptoProcessor.create(new SecretKey(properties.getSecretKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  SM2CryptoProcessor sm2CryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] SM2 Crypto Processor");
    return SM2CryptoProcessor.create(
        new SecretKey(properties.getPublicKeyHex(), properties.getPrivateKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  SM4CryptoProcessor sm4CryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] SM4 Crypto Processor");
    return SM4CryptoProcessor.create(
        new SecretKey(properties.getPublicKeyHex(), properties.getPrivateKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  HmacSHA256DigestProcessor hmacSHA256CryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] HmacSHA256 Digest Processor");
    return HmacSHA256DigestProcessor.create(new SecretKey(properties.getSecretKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  HmacSHA512DigestProcessor hmacSHA512CryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] HmacSHA512 Digest Processor");
    return HmacSHA512DigestProcessor.create(new SecretKey(properties.getSecretKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  RSACryptoProcessor rsaCryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] AES Digest Processor");
    return RSACryptoProcessor.create(
        new SecretKey(properties.getPublicKeyHex(), properties.getPrivateKeyHex()));
  }

  @Bean
  @ConditionalOnMissingBean
  SHA256DigestProcessor sha256CryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] SHA256 Digest Processor");
    return SHA256DigestProcessor.create(
        properties.getSalt(), properties.getSaltPosition(), properties.getDigestCount());
  }

  @Bean
  @ConditionalOnMissingBean
  SM3DigestProcessor sm3CryptoProcessor(CryptoProperties properties) {
    log.debug("[CRYPTO-HUTOOL] SM3 Digest Processor");
    return SM3DigestProcessor.create(
        properties.getSalt(), properties.getSaltPosition(), properties.getDigestCount());
  }
}
