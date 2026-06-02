package tutorials4j.framework.crypto.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.crypto.core.AsymmetricCryptoStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CRYPTO)
public class CryptoProperties {
  private AsymmetricCryptoStrategy asymmetricCryptoStrategy = AsymmetricCryptoStrategy.STANDARD;
  private String secretKeyHex;
  private String privateKeyHex;
  private String publicKeyHex;
  private String salt;
  private int saltPosition = 0;
  private int digestCount = 1;
}
