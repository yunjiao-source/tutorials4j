package tutorials4j.framework.oauth.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.auth.core.autoconfigure.OAuthConfiguration;
import tutorials4j.framework.auth.core.autoconfigure.OAuthMvcConfiguration;
import tutorials4j.framework.oauth.sotoken.autoconfigure.SoTokenOAuthConfiguration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({OAuthConfiguration.class, OAuthMvcConfiguration.class, SoTokenOAuthConfiguration.class})
public class OAuthAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH] OAuth Auto Configuration");
  }
}
