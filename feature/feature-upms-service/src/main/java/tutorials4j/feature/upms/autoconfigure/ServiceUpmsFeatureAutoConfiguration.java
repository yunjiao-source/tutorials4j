package tutorials4j.feature.upms.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import tutorials4j.feature.core.properties.UpmsFeatureProperties;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@EnableAsync
@EnableCaching
@AutoConfiguration
@ComponentScan(basePackages = {"tutorials4j.feature.upms.service"})
@EnableConfigurationProperties({
  UpmsFeatureProperties.class,
})
public class ServiceUpmsFeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-FEATURE-UPMS-SERVICE] Service UPMS Feature Auto Configuration");
  }
}
