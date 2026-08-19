package tutorials4j.framework.crypto.core.bean;

import lombok.Getter;

/**
 * 非对称加解密策略枚举，定义框架支持的非对称加密算法及其对应的加密类别。
 *
 * @author Yun Jiao
 */
@Getter
public enum AsymmetricCryptoStrategy {
  /** RSA 非对称加密算法，对应 {@link CryptoCategory#RSA}。 */
  RSA(CryptoCategory.RSA),
  /** SM2 国密非对称加密算法，对应 {@link CryptoCategory#SM2}。 */
  SM(CryptoCategory.SM2);

  /** 该策略对应的加密类别。 */
  private final CryptoCategory category;

  AsymmetricCryptoStrategy(CryptoCategory category) {
    this.category = category;
  }
}
