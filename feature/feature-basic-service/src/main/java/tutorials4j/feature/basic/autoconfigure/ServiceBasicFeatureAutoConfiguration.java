package tutorials4j.feature.basic.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@EnableCaching
@AutoConfiguration
@ComponentScan(basePackages = {"tutorials4j.feature.basic.service"})
@EnableJpaRepositories(basePackages = {"tutorials4j.feature.basic.repository"})
@EntityScan(basePackages = {"tutorials4j.feature.basic.entity"})
public class ServiceBasicFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-BASIC-SERVICE] Service Basic Feature Auto Configuration");
  }
}
