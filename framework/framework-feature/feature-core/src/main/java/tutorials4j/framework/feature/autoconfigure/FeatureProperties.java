package tutorials4j.framework.feature.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE)
public class FeatureProperties {
  private boolean signInEnabled = false;
  private boolean captchaEnabled = false;
}
