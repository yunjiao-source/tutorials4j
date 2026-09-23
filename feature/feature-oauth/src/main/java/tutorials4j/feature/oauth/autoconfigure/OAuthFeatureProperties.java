package tutorials4j.feature.oauth.autoconfigure;

import java.time.Duration;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_FEATURE_OAUTH)
public class OAuthFeatureProperties {
  private Duration defaultAccountExpire = Duration.ofDays(3650);
  private Duration defaultCredentialsExpire = Duration.ofDays(90);
}
