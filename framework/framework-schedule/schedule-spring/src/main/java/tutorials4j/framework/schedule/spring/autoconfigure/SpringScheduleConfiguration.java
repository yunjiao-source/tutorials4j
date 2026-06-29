package tutorials4j.framework.schedule.spring.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.schedule.spring.component.ScheduleService;
import tutorials4j.framework.schedule.spring.component.ScheduleTaskManager;
import tutorials4j.framework.schedule.spring.handler.TaskRuntimeDataHandler;
import tutorials4j.framework.schedule.spring.properties.SpringScheduleProperties;
import tutorials4j.framework.schedule.spring.repository.TaskRepository;
import tutorials4j.framework.schedule.spring.repository.YamlTaskRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_SPRING,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties({
  SpringScheduleProperties.class,
})
public class SpringScheduleConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-SPRING] Schedule Spring Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  TaskRepository<?> yamlTaskRepository(SpringScheduleProperties properties) {
    log.trace("[SCHEDULE-SPRING] Yaml Task Repository");
    return new YamlTaskRepository(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  ScheduleTaskManager scheduleTaskManager(
      TaskRepository<?> taskRepository,
      ObjectProvider<TaskRuntimeDataHandler> handlers,
      SpringScheduleProperties properties) {
    log.trace("[SCHEDULE-SPRING] Schedule Task Manager");
    return new ScheduleTaskManager(taskRepository, handlers.orderedStream().toList(), properties);
  }

  @Bean
  @ConditionalOnMissingBean
  ScheduleService scheduleService(
      ScheduleTaskManager scheduleTaskManager, TaskRepository<?> taskRepository) {
    log.trace("[FEATURE-SPRING] Schedule Service");
    return new ScheduleService(scheduleTaskManager, taskRepository);
  }
}
