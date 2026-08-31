package tutorials4j.framework.schedule.spring.autoconfigure;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.schedule.spring.component.ScheduleService;
import tutorials4j.framework.schedule.spring.component.ScheduleTaskManager;
import tutorials4j.framework.schedule.spring.handler.TaskRuntimeDataHandler;
import tutorials4j.framework.schedule.spring.metrics.MicrometerTaskRuntimeDataHandler;
import tutorials4j.framework.schedule.spring.properties.SpringScheduleProperties;
import tutorials4j.framework.schedule.spring.repository.TaskRepository;
import tutorials4j.framework.schedule.spring.repository.YamlTaskRepository;

/**
 * Spring 定时调度功能的自动配置类。
 *
 * <p>在配置属性 {@code PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_SPRING} 对应的 enabled 开关开启时生效， 注册 YAML
 * 任务仓库、任务管理器与任务操作服务等 Bean。
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
  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-SPRING] Schedule Spring Configuration");
  }

  /**
   * 注册基于 YAML 配置的任务仓库 Bean。
   *
   * @param properties Spring 定时任务配置属性
   * @return 任务仓库实例
   */
  @Bean
  @ConditionalOnMissingBean
  TaskRepository<?> yamlTaskRepository(SpringScheduleProperties properties) {
    log.trace("[SCHEDULE-SPRING] Yaml Task Repository");
    return new YamlTaskRepository(properties);
  }

  /**
   * 注册定时任务管理器 Bean。
   *
   * @param taskRepository 任务数据仓库
   * @param handlers 任务运行数据处理器提供者（按顺序）
   * @param properties Spring 定时任务配置属性
   * @return 定时任务管理器实例
   */
  @Bean
  @ConditionalOnMissingBean
  ScheduleTaskManager scheduleTaskManager(
      TaskRepository<?> taskRepository,
      ObjectProvider<TaskRuntimeDataHandler> handlers,
      SpringScheduleProperties properties) {
    log.trace("[SCHEDULE-SPRING] Schedule Task Manager");
    return new ScheduleTaskManager(taskRepository, handlers.orderedStream().toList(), properties);
  }

  /**
   * 注册定时任务操作服务 Bean。
   *
   * @param scheduleTaskManager 定时任务管理器
   * @param taskRepository 任务数据仓库
   * @return 定时任务操作服务实例
   */
  @Bean
  @ConditionalOnMissingBean
  ScheduleService scheduleService(
      ScheduleTaskManager scheduleTaskManager, TaskRepository<?> taskRepository) {
    log.trace("[FEATURE-SPRING] Schedule Service");
    return new ScheduleService(scheduleTaskManager, taskRepository);
  }

  /**
   * 定时任务监控的自动配置类。
   *
   * <p>在存在 {@link MeterRegistry} 与 {@link ScheduleTaskManager} Bean 时生效， 注册 {@link
   * MicrometerTaskRuntimeDataHandler} 以将任务运行数据上报到 Micrometer 指标。
   *
   * @author Yun Jiao
   */
  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnBean({MeterRegistry.class})
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_SCHEDULE_SPRING + ".metrics",
      name = PropertiesConsts.PROPERTY_ENABLED,
      havingValue = "true",
      matchIfMissing = true)
  public static class SpringScheduleMetricsConfiguration {
    @PostConstruct
    public void postConstruct() {
      log.trace("[SCHEDULE-SPRING] Spring Schedule Metrics Configuration");
    }

    /**
     * 注册任务监控指标处理器 Bean。
     *
     * @param meterRegistry Micrometer 指标注册中心
     * @return 任务监控指标处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    MicrometerTaskRuntimeDataHandler micrometerTaskRuntimeDataHandler(
        MeterRegistry meterRegistry, SpringScheduleProperties properties) {
      log.trace("[SCHEDULE-SPRING] Micrometer Task Runtime DataHandler");
      return new MicrometerTaskRuntimeDataHandler(meterRegistry, properties.getMetrics());
    }
  }
}
