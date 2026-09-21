package tutorials4j.microservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_MICROSERVICE_OAUTH)
public class OAuthMicroserviceProperties {
  private boolean enableDefaultScopeHandlerInterface = false;
}
