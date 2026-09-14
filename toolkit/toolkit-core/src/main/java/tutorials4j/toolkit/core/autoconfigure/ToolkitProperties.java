package tutorials4j.toolkit.core.autoconfigure;

import java.time.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT)
public class ToolkitProperties {
  private ScheduledExecutionOptions scheduledThreadPoolExecutor = new ScheduledExecutionOptions();
  private ThreadExecutionOptions threadPoolExecutor = new ThreadExecutionOptions();

  @Data
  public static class ScheduledExecutionOptions {
    private Boolean daemon;
    private Integer corePoolSize;
    private String threadNamePrefix;
    private Boolean awaitTermination;
    private Duration awaitTerminationPeriod;

    public ScheduledExecutionOptions() {}

    public ScheduledExecutionOptions(
        Boolean daemon,
        Integer corePoolSize,
        String threadNamePrefix,
        Boolean awaitTermination,
        Duration awaitTerminationPeriod) {
      this.daemon = daemon;
      this.corePoolSize = corePoolSize;
      this.threadNamePrefix = threadNamePrefix;
      this.awaitTermination = awaitTermination;
      this.awaitTerminationPeriod = awaitTerminationPeriod;
    }
  }

  @Getter
  @Setter
  public static class ThreadExecutionOptions extends ScheduledExecutionOptions {
    private Integer maximumPoolSize;
    private Integer queueCapacity;
    private Boolean allowCoreThreadTimeOut;
    private Duration keepAlive;

    public ThreadExecutionOptions() {}

    public ThreadExecutionOptions(
        Boolean daemon,
        Integer corePoolSize,
        String threadNamePrefix,
        Boolean awaitTermination,
        Duration awaitTerminationPeriod,
        Integer maximumPoolSize,
        Integer queueCapacity,
        Boolean allowCoreThreadTimeOut,
        Duration keepAlive) {
      super(daemon, corePoolSize, threadNamePrefix, awaitTermination, awaitTerminationPeriod);
      this.maximumPoolSize = maximumPoolSize;
      this.queueCapacity = queueCapacity;
      this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
      this.keepAlive = keepAlive;
    }
  }
}
