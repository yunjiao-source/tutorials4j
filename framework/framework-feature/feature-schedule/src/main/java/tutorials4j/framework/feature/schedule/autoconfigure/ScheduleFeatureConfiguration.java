package tutorials4j.framework.feature.schedule.autoconfigure;

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
@ComponentScan(
    basePackages = {
      "tutorials4j.framework.feature.schedule.domain",
      "tutorials4j.framework.feature.schedule.web"
    })
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.schedule.domain"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.schedule.domain"})
public class ScheduleFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SCHEDULE] Schedule Feature Configuration");
  }
}
