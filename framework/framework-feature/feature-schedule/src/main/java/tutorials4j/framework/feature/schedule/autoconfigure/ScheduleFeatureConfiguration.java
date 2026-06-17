package tutorials4j.framework.feature.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.feature.schedule.AsyncEventConsumerContainer;
import tutorials4j.framework.schedule.core.component.ChangeStatusEventConsumer;
import tutorials4j.framework.schedule.core.component.EventConsumerContainer;

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

  @Bean
  EventConsumerContainer AsyncEventConsumerContainer(
      ObjectProvider<ChangeStatusEventConsumer> consumers) {
    log.debug("[FEATURE-SCHEDULE] Async Event Consumer Container");
    return new AsyncEventConsumerContainer(consumers.orderedStream().collect(Collectors.toList()));
  }
}
