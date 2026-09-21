package tutorials4j.feature.oauth.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@ComponentScan(
    basePackages = {"tutorials4j.feature.oauth.service", "tutorials4j.feature.oauth.component"})
@EnableJpaRepositories(basePackages = {"tutorials4j.feature.oauth.repository"})
@EntityScan(basePackages = {"tutorials4j.feature.oauth.entity"})
public class OAuthFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-OAUTH] OAuth Feature Auto Configuration");
  }
}
