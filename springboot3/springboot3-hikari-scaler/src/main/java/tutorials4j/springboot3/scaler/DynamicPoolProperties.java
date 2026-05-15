package tutorials4j.springboot3.scaler;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 动态池配置
 *
 * @author Yun Jiao
 */
@Data
@Component
@ConfigurationProperties(prefix = "dynamic.pool")
public class DynamicPoolProperties {
  private boolean enabled = true;

  // 扩容配置
  private ScaleUp scaleUp = new ScaleUp();

  // 缩容配置
  private ScaleDown scaleDown = new ScaleDown();

  // 监控配置
  private Monitor monitor = new Monitor();

  // 告警配置
  private Alert alert = new Alert();

  // 扩容配置
  @Data
  public static class ScaleUp {
    private boolean enabled = true;
    // 使用率超过80%触发扩容
    private Integer thresholdPercentage = 80;
    // 等待队列超过5触发扩容
    private Integer queueLengthThreshold = 5;
    // 每次增加10个连接
    private Integer incrementSize = 10;
    // 最大连接数上限
    private Integer maxPoolSize = 100;
    // 扩容冷却时间（秒）
    private Duration cooldownTime = Duration.ofSeconds(60);
  }

  // 缩容配置
  @Data
  public static class ScaleDown {
    private boolean enabled = true;
    // 使用率低于30%触发缩容
    private Integer thresholdPercentage = 30;
    // 最小连接数下限
    private Integer minPoolSize = 5;
    // 每次减少5个连接
    private Integer decrementSize = 5;
    // 持续空闲5分钟才缩容
    private Duration idleTime = Duration.ofMinutes(5);
  }

  // 监控配置
  @Data
  public static class Monitor {
    private boolean enabled = true;
    // 监控间隔
    private Duration intervalTime = Duration.ofSeconds(10);
    // 指标保留时间
    private Duration metricsRetentionTime = Duration.ofMinutes(60);
  }

  // 告警配置
  @Data
  public static class Alert {
    private boolean enabled = true;
    // 使用率超过90%告警
    private Integer highUsageThreshold = 90;
    // 等待时间超过1000ms告警
    private Duration queueWaitThresholdTime = Duration.ofSeconds(1);
    private String channels = "email,dingtalk";
  }
}
