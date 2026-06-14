package tutorials4j.framework.schedule.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.schedule.core.autoconfigure.ScheduleConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({ScheduleConfiguration.class})
public class ScheduleAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[SCHEDULE] Schedule Auto Configuration");
  }
}
