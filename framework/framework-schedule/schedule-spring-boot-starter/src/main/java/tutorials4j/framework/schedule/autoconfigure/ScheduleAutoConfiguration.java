package tutorials4j.framework.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.schedule.core.autoconfigure.MonitorScheduleConfiguration;
import tutorials4j.framework.schedule.core.autoconfigure.ScheduleConfiguration;
import tutorials4j.framework.schedule.powerjob.autoconfigure.PowerJobWorkerScheduleConfiguration;
import tutorials4j.framework.schedule.xxljob.autoconfigure.XxlJobScheduleConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  ScheduleConfiguration.class,
  MonitorScheduleConfiguration.class,
  PowerJobWorkerScheduleConfiguration.class,
  XxlJobScheduleConfiguration.class
})
public class ScheduleAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE] Schedule Auto Configuration");
  }
}
