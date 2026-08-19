package tutorials4j.framework.schedule.spring.properties;

import java.time.Duration;
import java.time.Instant;
import lombok.Data;

/**
 * 任务执行选项配置。
 *
 * <p>描述单个任务的执行参数，包括初始延迟、最大失败次数、最大执行次数与截止时间。
 *
 * @author Yun Jiao
 */
@Data
public class TaskExecutionOptions {
  /** 任务首次执行的初始延迟时间，默认 30 秒。 */
  private Duration initialDelay = Duration.ofSeconds(30);

  /** 最大允许失败次数，超过后任务将停止调度；为 {@code null} 表示不限制。 */
  private Integer maxFailureCount;

  /** 最大允许执行次数，达到后任务将停止调度；为 {@code null} 表示不限制。 */
  private Integer MaxExecutionCount;

  /** 任务执行的截止时间，超过后任务将不再执行；为 {@code null} 表示不限制。 */
  private Instant dueDate;
}
