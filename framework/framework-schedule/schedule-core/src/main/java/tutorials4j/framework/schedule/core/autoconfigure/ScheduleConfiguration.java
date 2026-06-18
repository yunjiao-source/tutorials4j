package tutorials4j.framework.schedule.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.schedule.core.component.AsyncEventConsumerContainer;
import tutorials4j.framework.schedule.core.component.ChangeStatusEventConsumer;
import tutorials4j.framework.schedule.core.component.EventConsumerContainer;
import tutorials4j.framework.schedule.core.component.ScheduleTaskManager;
import tutorials4j.framework.schedule.core.component.SyncEventConsumerContainer;
import tutorials4j.framework.schedule.core.properties.ScheduleProperties;
import tutorials4j.framework.schedule.core.repository.TaskRepository;
import tutorials4j.framework.schedule.core.repository.YamlTaskRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  ScheduleProperties.class,
})
public class ScheduleConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[SCHEDULE-CORE] Schedule Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  TaskRepository<?> yamlTaskRepository(ScheduleProperties properties) {
    log.debug("[SCHEDULE-CORE] Yaml Task Repository");
    return new YamlTaskRepository(properties);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE,
      name = "event-consumer-type",
      havingValue = "sync",
      matchIfMissing = true)
  EventConsumerContainer syncEventConsumerContainer(
      ObjectProvider<ChangeStatusEventConsumer> consumers) {
    log.debug("[SCHEDULE-CORE] Sync Event Consumer Container");
    return new SyncEventConsumerContainer(consumers.orderedStream().collect(Collectors.toList()));
  }

  @Bean
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE,
      name = "event-consumer-type",
      havingValue = "async")
  EventConsumerContainer AsyncEventConsumerContainer(
      ObjectProvider<ChangeStatusEventConsumer> consumers) {
    log.debug("[SCHEDULE-CORE] Async Event Consumer Container");
    return new AsyncEventConsumerContainer(consumers.orderedStream().collect(Collectors.toList()));
  }

  @Bean
  @ConditionalOnMissingBean
  ScheduleTaskManager scheduleTaskManager(
      TaskRepository<?> taskRepository,
      EventConsumerContainer syncEventConsumerContainer,
      ScheduleProperties properties) {
    log.debug("[SCHEDULE-CORE] Schedule Task Manager");
    return new ScheduleTaskManager(taskRepository, syncEventConsumerContainer, properties);
  }
}
