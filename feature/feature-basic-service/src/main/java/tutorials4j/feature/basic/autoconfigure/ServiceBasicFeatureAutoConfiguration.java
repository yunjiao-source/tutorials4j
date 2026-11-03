package tutorials4j.feature.basic.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@EnableCaching
@AutoConfiguration
@ComponentScan(basePackages = {"tutorials4j.feature.basic.service"})
public class ServiceBasicFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-BASIC-SERVICE] Service Basic Feature Auto Configuration");
  }
}
