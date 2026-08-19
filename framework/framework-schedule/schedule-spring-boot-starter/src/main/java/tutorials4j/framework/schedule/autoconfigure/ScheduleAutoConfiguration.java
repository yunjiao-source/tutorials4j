package tutorials4j.framework.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.schedule.powerjob.autoconfigure.PowerJobWorkerScheduleConfiguration;
import tutorials4j.framework.schedule.spring.autoconfigure.MonitorScheduleConfiguration;
import tutorials4j.framework.schedule.spring.autoconfigure.SpringScheduleConfiguration;
import tutorials4j.framework.schedule.xxljob.autoconfigure.XxlJobScheduleConfiguration;

/**
 * 调度模块自动配置入口，导入 Spring 调度、监控调度、PowerJob 与 XXL-JOB 调度配置。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  SpringScheduleConfiguration.class,
  MonitorScheduleConfiguration.class,
  PowerJobWorkerScheduleConfiguration.class,
  XxlJobScheduleConfiguration.class
})
public class ScheduleAutoConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[SCHEDULE] Schedule Auto Configuration");
  }
}
