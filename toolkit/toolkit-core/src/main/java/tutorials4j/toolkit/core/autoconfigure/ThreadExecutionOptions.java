package tutorials4j.toolkit.core.autoconfigure;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class ThreadExecutionOptions extends ScheduledExecutionOptions {
  private Integer maximumPoolSize;
  private Integer queueCapacity;
  private Boolean allowCoreThreadTimeOut;
  private Duration keepAlive;

  public static ThreadExecutionOptions withDefaults(ThreadExecutionOptions options) {
    int core = Runtime.getRuntime().availableProcessors() + 1;
    ThreadExecutionOptions defaultOptions = new ThreadExecutionOptions();
    defaultOptions.setDaemon(false);
    defaultOptions.setCorePoolSize(core);
    defaultOptions.setThreadNamePrefix("thread-pool-");
    defaultOptions.setAwaitTermination(true);
    defaultOptions.setAwaitTerminationPeriod(Duration.ofSeconds(30));
    defaultOptions.setMaximumPoolSize(core * 2);
    defaultOptions.setQueueCapacity(1000);
    defaultOptions.setAllowCoreThreadTimeOut(true);
    defaultOptions.setKeepAlive(Duration.ofHours(1));
    BeanUtil.copyProperties(options, defaultOptions, CopyOptions.create().setIgnoreNullValue(true));
    return defaultOptions;
  }
}
