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
public class ThreadExecutionOptionsTest {
  @Test
  @DisplayName("入参为 null 时返回全部默认值（含线程池专属字段）")
  void shouldReturnAllDefaultsWhenOptionsIsNull() {
    ThreadExecutionOptions result = ThreadExecutionOptions.withDefaults(null);

    int expectedCore = Runtime.getRuntime().availableProcessors() + 1;
    assertThat(result).isNotNull();
    assertThat(result.getDaemon()).isFalse();
    assertThat(result.getCorePoolSize()).isEqualTo(expectedCore);
    assertThat(result.getThreadNamePrefix()).isEqualTo("thread-pool-");
    assertThat(result.getAwaitTermination()).isTrue();
    assertThat(result.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(30));
    assertThat(result.getMaximumPoolSize()).isEqualTo(expectedCore * 2);
    assertThat(result.getQueueCapacity()).isEqualTo(1000);
    assertThat(result.getAllowCoreThreadTimeOut()).isTrue();
    assertThat(result.getKeepAlive()).isEqualTo(Duration.ofHours(1));
  }

  @Test
  @DisplayName("字段为 null 时使用默认值")
  void shouldKeepDefaultsWhenFieldsAreNull() {
    ThreadExecutionOptions options = new ThreadExecutionOptions();

    ThreadExecutionOptions result = ThreadExecutionOptions.withDefaults(options);

    int expectedCore = Runtime.getRuntime().availableProcessors() + 1;
    assertThat(result.getThreadNamePrefix()).isEqualTo("thread-pool-");
    assertThat(result.getCorePoolSize()).isEqualTo(expectedCore);
    assertThat(result.getMaximumPoolSize()).isEqualTo(expectedCore * 2);
    assertThat(result.getQueueCapacity()).isEqualTo(1000);
    assertThat(result.getKeepAlive()).isEqualTo(Duration.ofHours(1));
  }

  @Test
  @DisplayName("所有非 null 字段覆盖默认值")
  void shouldOverrideAllDefaults() {
    ThreadExecutionOptions options = new ThreadExecutionOptions();
    options.setDaemon(true);
    options.setCorePoolSize(2);
    options.setThreadNamePrefix("biz-thread-");
    options.setAwaitTermination(false);
    options.setAwaitTerminationPeriod(Duration.ofSeconds(10));
    options.setMaximumPoolSize(16);
    options.setQueueCapacity(500);
    options.setAllowCoreThreadTimeOut(false);
    options.setKeepAlive(Duration.ofMinutes(30));

    ThreadExecutionOptions result = ThreadExecutionOptions.withDefaults(options);

    assertThat(result.getDaemon()).isTrue();
    assertThat(result.getCorePoolSize()).isEqualTo(2);
    assertThat(result.getThreadNamePrefix()).isEqualTo("biz-thread-");
    assertThat(result.getAwaitTermination()).isFalse();
    assertThat(result.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(10));
    assertThat(result.getMaximumPoolSize()).isEqualTo(16);
    assertThat(result.getQueueCapacity()).isEqualTo(500);
    assertThat(result.getAllowCoreThreadTimeOut()).isFalse();
    assertThat(result.getKeepAlive()).isEqualTo(Duration.ofMinutes(30));
  }

  @Test
  @DisplayName("仅覆盖继承自父类的字段时，子类字段仍为默认值")
  void shouldMergeInheritedFieldsWithDefaultSubclassFields() {
    ThreadExecutionOptions options = new ThreadExecutionOptions();
    options.setCorePoolSize(6);
    options.setThreadNamePrefix("mixed-pool-");

    ThreadExecutionOptions result = ThreadExecutionOptions.withDefaults(options);

    int expectedCore = Runtime.getRuntime().availableProcessors() + 1;
    assertThat(result.getCorePoolSize()).isEqualTo(6);
    assertThat(result.getThreadNamePrefix()).isEqualTo("mixed-pool-");
    assertThat(result.getMaximumPoolSize()).isEqualTo(expectedCore * 2);
    assertThat(result.getQueueCapacity()).isEqualTo(1000);
    assertThat(result.getAllowCoreThreadTimeOut()).isTrue();
    assertThat(result.getKeepAlive()).isEqualTo(Duration.ofHours(1));
  }

  @Test
  @DisplayName("不修改原始入参对象")
  void shouldNotMutateInput() {
    ThreadExecutionOptions options = new ThreadExecutionOptions();
    options.setCorePoolSize(3);

    ThreadExecutionOptions result = ThreadExecutionOptions.withDefaults(options);

    assertThat(result).isNotSameAs(options);
    assertThat(options.getCorePoolSize()).isEqualTo(3);
    assertThat(options.getThreadNamePrefix()).isNull();
    assertThat(options.getMaximumPoolSize()).isNull();
    assertThat(options.getQueueCapacity()).isNull();
    assertThat(options.getKeepAlive()).isNull();
  }

  @Test
  @DisplayName("继承关系校验：ThreadExecutionOptions 是 ScheduledExecutionOptions 的子类")
  void shouldExtendScheduledExecutionOptions() {
    ThreadExecutionOptions options = new ThreadExecutionOptions();
    assertThat(options).isInstanceOf(ScheduledExecutionOptions.class);
  }
}
