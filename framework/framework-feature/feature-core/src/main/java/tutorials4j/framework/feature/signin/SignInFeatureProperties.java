package tutorials4j.framework.feature.signin;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE_SIGN_IN)
public class SignInFeatureProperties {
  private String redisKeyPrefix = "sign-in:";
  private Duration expireTime = Duration.ofDays(365);
}
