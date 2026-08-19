package tutorials4j.framework.crypto.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.crypto.core.bean.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.bean.SymmetricCryptoStrategy;

/**
 * 加密功能配置属性。
 *
 * <p>包含非对称/对称加密策略、密钥（十六进制）、加盐值及摘要次数等配置。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CRYPTO)
public class CryptoProperties {
  /** 非对称加密策略，默认 RSA。 */
  private AsymmetricCryptoStrategy asymmetricCryptoStrategy = AsymmetricCryptoStrategy.RSA;

  /** 对称加密策略，默认 AES。 */
  private SymmetricCryptoStrategy symmetricCryptoStrategy = SymmetricCryptoStrategy.AES;

  /** 对称加密密钥（十六进制）。 */
  private String secretKeyHex;

  /** 非对称加密私钥（十六进制）。 */
  private String privateKeyHex;

  /** 非对称加密公钥（十六进制）。 */
  private String publicKeyHex;

  /** 加盐值。 */
  private String salt;

  /** 盐值在密文中的插入位置。 */
  private int saltPosition = 0;

  /** 摘要计算次数。 */
  private int digestCount = 1;
}
