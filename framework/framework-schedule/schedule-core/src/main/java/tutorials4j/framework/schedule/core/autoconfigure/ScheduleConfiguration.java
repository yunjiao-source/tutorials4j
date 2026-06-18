package tutorials4j.framework.schedule.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.schedule.core.component.LoggingTaskRuntimeDataHandler;
import tutorials4j.framework.schedule.core.component.ScheduleTaskManager;
import tutorials4j.framework.schedule.core.component.TaskRuntimeDataHandler;
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
    log.trace("[SCHEDULE-CORE] Schedule Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  TaskRepository<?> yamlTaskRepository(ScheduleProperties properties) {
    log.trace("[SCHEDULE-CORE] Yaml Task Repository");
    return new YamlTaskRepository(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  TaskRuntimeDataHandler loggingTaskRuntimeDataHandler() {
    log.trace("[SCHEDULE-CORE] Logging Task Runtime Data Handler");
    return new LoggingTaskRuntimeDataHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  ScheduleTaskManager scheduleTaskManager(
      TaskRepository<?> taskRepository,
      TaskRuntimeDataHandler taskRuntimeDataHandler,
      ScheduleProperties properties) {
    log.trace("[SCHEDULE-CORE] Schedule Task Manager");
    return new ScheduleTaskManager(taskRepository, taskRuntimeDataHandler, properties);
  }
}
