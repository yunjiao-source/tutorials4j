package tutorials4j.toolkit.core.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ExecutionOptionsTest {

  @Test
  @DisplayName("入参为 null 时返回全部默认值")
  void shouldReturnAllDefaultsWhenOptionsIsNull() {
    ScheduledExecutionOptions result = ScheduledExecutionOptions.withDefaults(null);

    int expectedCore = Runtime.getRuntime().availableProcessors() + 1;
    assertThat(result).isNotNull();
    assertThat(result.getDaemon()).isFalse();
    assertThat(result.getCorePoolSize()).isEqualTo(expectedCore);
    assertThat(result.getThreadNamePrefix()).isEqualTo("schedule-pool-");
    assertThat(result.getAwaitTermination()).isTrue();
    assertThat(result.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(30));
  }

  @Test
  @DisplayName("字段为 null 时使用默认值")
  void shouldKeepDefaultsWhenFieldsAreNull() {
    ScheduledExecutionOptions options = new ScheduledExecutionOptions();

    ScheduledExecutionOptions result = ScheduledExecutionOptions.withDefaults(options);

    int expectedCore = Runtime.getRuntime().availableProcessors() + 1;
    assertThat(result.getDaemon()).isFalse();
    assertThat(result.getCorePoolSize()).isEqualTo(expectedCore);
    assertThat(result.getThreadNamePrefix()).isEqualTo("schedule-pool-");
    assertThat(result.getAwaitTermination()).isTrue();
    assertThat(result.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(30));
  }

  @Test
  @DisplayName("非 null 字段覆盖默认值")
  void shouldOverrideDefaultsWithProvidedValues() {
    ScheduledExecutionOptions options = new ScheduledExecutionOptions();
    options.setDaemon(true);
    options.setCorePoolSize(8);
    options.setThreadNamePrefix("custom-sched-");
    options.setAwaitTermination(false);
    options.setAwaitTerminationPeriod(Duration.ofMinutes(5));

    ScheduledExecutionOptions result = ScheduledExecutionOptions.withDefaults(options);

    assertThat(result.getDaemon()).isTrue();
    assertThat(result.getCorePoolSize()).isEqualTo(8);
    assertThat(result.getThreadNamePrefix()).isEqualTo("custom-sched-");
    assertThat(result.getAwaitTermination()).isFalse();
    assertThat(result.getAwaitTerminationPeriod()).isEqualTo(Duration.ofMinutes(5));
  }

  @Test
  @DisplayName("部分字段覆盖时其余字段保持默认值")
  void shouldMergePartialOverridesWithDefaults() {
    ScheduledExecutionOptions options = new ScheduledExecutionOptions();
    options.setDaemon(true);
    options.setCorePoolSize(4);

    ScheduledExecutionOptions result = ScheduledExecutionOptions.withDefaults(options);

    assertThat(result.getDaemon()).isTrue();
    assertThat(result.getCorePoolSize()).isEqualTo(4);
    assertThat(result.getThreadNamePrefix()).isEqualTo("schedule-pool-");
    assertThat(result.getAwaitTermination()).isTrue();
    assertThat(result.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(30));
  }

  @Test
  @DisplayName("不修改原始入参对象")
  void shouldNotMutateInput() {
    ScheduledExecutionOptions options = new ScheduledExecutionOptions();

    ScheduledExecutionOptions result = ScheduledExecutionOptions.withDefaults(options);

    assertThat(result).isNotSameAs(options);
    assertThat(options.getDaemon()).isNull();
    assertThat(options.getCorePoolSize()).isNull();
    assertThat(options.getThreadNamePrefix()).isNull();
    assertThat(options.getAwaitTermination()).isNull();
    assertThat(options.getAwaitTerminationPeriod()).isNull();
  }
}
