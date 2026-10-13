package tutorials4j.toolkit.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.core.autoconfigure.ScheduledExecutionOptions;

/**
 * {@link ScheduledThreadPoolExecutorHolder} 单元测试。
 *
 * <p>Holder 是进程内单例（{@link ScheduledThreadPoolExecutorHolder#instance}），每个用例前后通过反射清空
 * 其内部状态，保证用例之间互不影响。
 *
 * @author YunJiao Yang
 */
class ScheduledThreadPoolExecutorHolderTest {
  private final ScheduledThreadPoolExecutorHolder holder =
      ScheduledThreadPoolExecutorHolder.instance;

  @BeforeEach
  void setUp() {
    resetHolder();
  }

  @AfterEach
  void tearDown() {
    shutdownExecutorQuietly();
    resetHolder();
  }

  private void shutdownExecutorQuietly() {
    ScheduledThreadPoolExecutor executor = holder.getExecutor();
    if (executor != null) {
      executor.shutdownNow();
    }
  }

  private void resetHolder() {
    setField("executor", null);
    setField("options", null);
  }

  private void setField(String name, Object value) {
    try {
      Field field = ScheduledThreadPoolExecutorHolder.class.getDeclaredField(name);
      field.setAccessible(true);
      field.set(holder, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("重置 ScheduledThreadPoolExecutorHolder 状态失败: " + name, e);
    }
  }

  private static ScheduledExecutionOptions options() {
    ScheduledExecutionOptions options = new ScheduledExecutionOptions();
    options.setDaemon(true);
    options.setCorePoolSize(1);
    options.setThreadNamePrefix("test-schedule-");
    options.setAwaitTermination(true);
    options.setAwaitTerminationPeriod(Duration.ofSeconds(5));
    return options;
  }

  @Test
  @DisplayName("options 为 null 时抛出 IllegalArgumentException")
  void shouldRejectNullOptions() {
    assertThatThrownBy(() -> holder.initExecutor(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("options must not be null");
  }

  @Test
  @DisplayName("初始化前 executor 为 null，shutdown 是空操作")
  void shouldKeepNullExecutorBeforeInit() {
    assertThat(holder.getExecutor()).isNull();

    holder.shutdown();

    assertThat(holder.getExecutor()).isNull();
  }

  @Test
  @DisplayName("按配置创建计划任务线程池并设置关闭相关策略")
  void shouldApplyOptionsToExecutor() {
    holder.initExecutor(options());

    ScheduledThreadPoolExecutor executor = holder.getExecutor();

    assertThat(executor).isNotNull();
    assertThat(executor.getCorePoolSize()).isEqualTo(1);
    assertThat(executor.getThreadFactory()).isInstanceOf(SimpleNamedThreadFactory.class);
    assertThat(executor.getRemoveOnCancelPolicy()).isTrue();
    assertThat(executor.getExecuteExistingDelayedTasksAfterShutdownPolicy()).isFalse();
    assertThat(executor.getContinueExistingPeriodicTasksAfterShutdownPolicy()).isFalse();
  }

  @Test
  @DisplayName("配置为空时回退默认值：核心数为 CPU 数 + 1")
  void shouldApplyDefaultsForUnsetOptions() {
    holder.initExecutor(new ScheduledExecutionOptions());

    assertThat(holder.getExecutor().getCorePoolSize())
        .isEqualTo(Runtime.getRuntime().availableProcessors() + 1);
  }

  @Test
  @DisplayName("计划任务按延迟执行，工作线程使用配置的名称前缀与守护标记")
  void shouldExecuteScheduledTask() throws InterruptedException {
    holder.initExecutor(options());

    AtomicReference<String> threadName = new AtomicReference<>();
    AtomicReference<Boolean> daemon = new AtomicReference<>();
    CountDownLatch done = new CountDownLatch(1);

    holder
        .getExecutor()
        .schedule(
            () -> {
              threadName.set(Thread.currentThread().getName());
              daemon.set(Thread.currentThread().isDaemon());
              done.countDown();
            },
            10,
            TimeUnit.MILLISECONDS);

    assertThat(done.await(5, TimeUnit.SECONDS)).isTrue();
    assertThat(threadName.get()).isEqualTo("test-schedule--");
    assertThat(daemon.get()).isTrue();
  }

  @Test
  @DisplayName("重复初始化不会改变已创建的线程池")
  void shouldIgnoreLaterInitCalls() {
    holder.initExecutor(options());
    ScheduledThreadPoolExecutor first = holder.getExecutor();

    ScheduledExecutionOptions other = options();
    other.setCorePoolSize(4);
    holder.initExecutor(other);

    assertThat(holder.getExecutor()).isSameAs(first);
    assertThat(holder.getExecutor().getCorePoolSize()).isEqualTo(1);
  }

  @Test
  @DisplayName("并发初始化只会创建一个线程池")
  void shouldCreateSingleExecutorConcurrently() throws InterruptedException {
    int threads = 8;
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(threads);
    Set<ScheduledThreadPoolExecutor> executors = ConcurrentHashMap.newKeySet();

    for (int i = 0; i < threads; i++) {
      Thread thread =
          new Thread(
              () -> {
                try {
                  start.await();
                  holder.initExecutor(options());
                  executors.add(holder.getExecutor());
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                } finally {
                  finished.countDown();
                }
              });
      thread.start();
    }

    start.countDown();
    assertThat(finished.await(5, TimeUnit.SECONDS)).isTrue();

    assertThat(executors).hasSize(1);
    assertThat(holder.getExecutor()).isNotNull();
  }

  @Test
  @DisplayName("线程池关闭后提交计划任务被拒绝并抛出 RejectedExecutionException")
  void shouldRejectTaskAfterShutdown() {
    holder.initExecutor(options());
    ScheduledThreadPoolExecutor executor = holder.getExecutor();
    executor.shutdown();

    assertThatThrownBy(() -> executor.schedule(() -> {}, 1, TimeUnit.MILLISECONDS))
        .isInstanceOf(RejectedExecutionException.class)
        .hasMessageContaining("rejected from");
  }

  @Test
  @DisplayName("awaitTermination=true 时优雅关闭并等待线程池终结")
  void shouldShutdownAndAwaitTermination() {
    holder.initExecutor(options());
    ScheduledThreadPoolExecutor executor = holder.getExecutor();

    holder.shutdown();

    assertThat(executor.isShutdown()).isTrue();
    assertThat(executor.isTerminated()).isTrue();
  }

  @Test
  @DisplayName("awaitTermination=false 时直接强制关闭")
  void shouldForceShutdownWhenAwaitTerminationDisabled() {
    ScheduledExecutionOptions options = options();
    options.setAwaitTermination(false);
    holder.initExecutor(options);
    ScheduledThreadPoolExecutor executor = holder.getExecutor();

    holder.shutdown();

    assertThat(executor.isShutdown()).isTrue();
    assertThat(executor.isTerminated()).isTrue();
  }
}
