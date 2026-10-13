package tutorials4j.toolkit.core.autoconfigure;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.time.Duration;
import lombok.Data;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class ScheduledExecutionOptions {
  private Boolean daemon;
  private Integer corePoolSize;
  private String threadNamePrefix;
  private Boolean awaitTermination;
  private Duration awaitTerminationPeriod;

  public static ScheduledExecutionOptions withDefaults(ScheduledExecutionOptions options) {
    int core = Runtime.getRuntime().availableProcessors() + 1;
    ScheduledExecutionOptions defaultOptions = new ScheduledExecutionOptions();
    defaultOptions.setDaemon(false);
    defaultOptions.setCorePoolSize(core);
    defaultOptions.setThreadNamePrefix("schedule-pool-");
    defaultOptions.setAwaitTermination(true);
    defaultOptions.setAwaitTerminationPeriod(Duration.ofSeconds(30));
    BeanUtil.copyProperties(options, defaultOptions, CopyOptions.create().setIgnoreNullValue(true));
    return defaultOptions;
  }
}
