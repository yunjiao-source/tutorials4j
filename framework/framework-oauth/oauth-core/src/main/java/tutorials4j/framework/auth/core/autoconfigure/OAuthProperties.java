package tutorials4j.framework.auth.core.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.auth.core.common.OAuthStrategy;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_OAUTH)
public class OAuthProperties {
  private OAuthStrategy strategy = OAuthStrategy.SA_TOKEN;

  @NestedConfigurationProperty
  private ApiKeyRateLimiterOptions apiKeyRateLimiter = new ApiKeyRateLimiterOptions();
}
