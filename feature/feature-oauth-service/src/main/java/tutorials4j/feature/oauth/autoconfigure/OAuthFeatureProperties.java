package tutorials4j.feature.oauth.autoconfigure;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_FEATURE_OAUTH)
public class OAuthFeatureProperties {
  private Duration accountExpire = Duration.ofDays(90);
  private Duration credentialsExpire = Duration.ofDays(90);
  private ClearAuthHistoryOptions clearAuthHistory = new ClearAuthHistoryOptions();

  @Data
  public static class ClearAuthHistoryOptions {
    private Duration dataExpired = Duration.ofDays(90);
    private int maxLoopCount = 10;
    private int maxSizePreLoop = 999;
  }
}
