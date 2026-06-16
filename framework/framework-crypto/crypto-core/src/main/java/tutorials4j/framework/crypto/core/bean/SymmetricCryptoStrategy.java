package tutorials4j.framework.crypto.core.bean;

import lombok.Getter;

/**
 * 对称加解密策略
 *
 * @author Yun Jiao
 */
@Getter
public enum SymmetricCryptoStrategy {
  AES(CryptoCategory.AES),
  SM4(CryptoCategory.SM4);

  private final CryptoCategory category;

  SymmetricCryptoStrategy(CryptoCategory category) {
    this.category = category;
  }
}
