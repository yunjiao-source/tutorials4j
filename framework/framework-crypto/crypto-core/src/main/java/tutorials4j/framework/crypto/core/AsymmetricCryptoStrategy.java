package tutorials4j.framework.crypto.core;

import lombok.Getter;

/**
 * 对称加解密策略
 *
 * @author Yun Jiao
 */
@Getter
public enum AsymmetricCryptoStrategy {
  STANDARD(CryptoCategory.RSA),
  SM(CryptoCategory.SM2);

  private final CryptoCategory category;

  AsymmetricCryptoStrategy(CryptoCategory category) {
    this.category = category;
  }
}
