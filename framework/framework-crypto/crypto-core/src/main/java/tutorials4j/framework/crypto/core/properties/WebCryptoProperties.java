package tutorials4j.framework.crypto.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * Web 层加密功能配置属性，控制是否启用 Web 加密。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CRYPTO_WEB)
public class WebCryptoProperties {
  /** 是否启用 Web 层加密功能。 */
  private boolean enabled = false;
}
