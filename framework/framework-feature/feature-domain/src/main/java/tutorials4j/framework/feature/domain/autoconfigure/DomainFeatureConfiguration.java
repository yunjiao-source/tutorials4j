package tutorials4j.framework.feature.domain.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"tutorials4j.framework.feature.domain"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.domain"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.domain"})
public class DomainFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE-DOMAIN] Domain Feature Configuration");
  }
}
