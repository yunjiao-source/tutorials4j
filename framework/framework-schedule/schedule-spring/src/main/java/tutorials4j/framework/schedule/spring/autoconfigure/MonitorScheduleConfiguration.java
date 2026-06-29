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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnBean({MeterRegistry.class, ScheduleTaskManager.class})
public class MonitorScheduleConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE-SPRING] Monitor Schedule Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  MonitorTaskRuntimeDataHandler monitorTaskRuntimeDataHandler(MeterRegistry meterRegistry) {
    log.trace("[SCHEDULE-SPRING] Monitor Task Runtime Data Handler");
    return new MonitorTaskRuntimeDataHandler(meterRegistry);
  }
}
