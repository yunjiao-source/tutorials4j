package tutorials4j.framework.crypto.core.bean;

import lombok.Getter;

/**
 * 非对称加解密策略
 *
 * @author Yun Jiao
 */
@Getter
public enum AsymmetricCryptoStrategy {
  RSA(CryptoCategory.RSA),
  SM(CryptoCategory.SM2);

  private final CryptoCategory category;

  AsymmetricCryptoStrategy(CryptoCategory category) {
    this.category = category;
  }
}
