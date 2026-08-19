package tutorials4j.framework.crypto.core.bean;

import lombok.Getter;

/**
 * 对称加解密策略枚举，定义框架支持的对称加密算法及其对应的加密类别。
 *
 * @author Yun Jiao
 */
@Getter
public enum SymmetricCryptoStrategy {
  /** AES 对称加密算法，对应 {@link CryptoCategory#AES}。 */
  AES(CryptoCategory.AES),
  /** SM4 国密对称加密算法，对应 {@link CryptoCategory#SM4}。 */
  SM4(CryptoCategory.SM4);

  /** 该策略对应的加密类别。 */
  private final CryptoCategory category;

  SymmetricCryptoStrategy(CryptoCategory category) {
    this.category = category;
  }
}
