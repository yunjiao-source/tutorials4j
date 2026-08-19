package tutorials4j.framework.schedule.spring.autoconfigure;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.schedule.spring.component.ScheduleTaskManager;
import tutorials4j.framework.schedule.spring.handler.MonitorTaskRuntimeDataHandler;

/**
 * 定时任务监控的自动配置类。
 *
 * <p>在存在 {@link MeterRegistry} 与 {@link ScheduleTaskManager} Bean 时生效， 注册 {@link
 * MonitorTaskRuntimeDataHandler} 以将任务运行数据上报到 Micrometer 指标。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnBean({MeterRegistry.class, ScheduleTaskManager.class})
public class MonitorScheduleConfiguration {
  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-SPRING] Monitor Schedule Configuration");
  }

  /**
   * 注册任务监控指标处理器 Bean。
   *
   * @param meterRegistry Micrometer 指标注册中心
   * @return 任务监控指标处理器实例
   */
  @Bean
  @ConditionalOnMissingBean
  MonitorTaskRuntimeDataHandler monitorTaskRuntimeDataHandler(MeterRegistry meterRegistry) {
    log.trace("[SCHEDULE-SPRING] Monitor Task Runtime Data Handler");
    return new MonitorTaskRuntimeDataHandler(meterRegistry);
  }
}
