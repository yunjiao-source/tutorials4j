package tutorials4j.feature.oauth.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackages = {"tutorials4j.feature.oauth.controller"})
public class WebOAuthFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-OAUTH-WEB] Web OAuth Feature Auto Configuration");
  }
}
