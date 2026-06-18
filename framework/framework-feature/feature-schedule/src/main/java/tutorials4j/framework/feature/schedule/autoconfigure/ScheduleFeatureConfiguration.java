package tutorials4j.framework.feature.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.framework.common.core.PropertiesConsts;

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
@ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE, name = "schedule-enabled")
public class ScheduleFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE-SCHEDULE] Schedule Feature Configuration");
  }
}
