package tutorials4j.toolkit.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.core.autoconfigure.ThreadExecutionOptions;

/**
 * {@link ThreadPoolExecutorHolder} 单元测试。
 *
 * <p>Holder 是进程内单例（{@link ThreadPoolExecutorHolder#instance}），每个用例前后通过反射清空 其内部状态，保证用例之间互不影响。
 *
 * @author YunJiao Yang
 */
class ThreadPoolExecutorHolderTest {
  private final ThreadPoolExecutorHolder holder = ThreadPoolExecutorHolder.instance;

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
    ThreadPoolExecutor executor = holder.getExecutor();
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
      Field field = ThreadPoolExecutorHolder.class.getDeclaredField(name);
      field.setAccessible(true);
      field.set(holder, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("重置 ThreadPoolExecutorHolder 状态失败: " + name, e);
    }
  }

  private static ThreadExecutionOptions options() {
    ThreadExecutionOptions options = new ThreadExecutionOptions();
    options.setDaemon(true);
    options.setCorePoolSize(1);
    options.setMaximumPoolSize(2);
    options.setQueueCapacity(2);
    options.setKeepAlive(Duration.ofSeconds(60));
    options.setThreadNamePrefix("test-pool-");
    options.setAwaitTermination(true);
    options.setAwaitTerminationPeriod(Duration.ofSeconds(5));
    options.setAllowCoreThreadTimeOut(false);
    return options;
  }

  private static ThreadExecutionOptions smallPoolOptions() {
    ThreadExecutionOptions options = options();
    options.setMaximumPoolSize(1);
    options.setQueueCapacity(1);
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
  @DisplayName("按配置创建线程池：核心数、最大数、空闲时间与队列容量")
  void shouldApplyOptionsToExecutor() {
    holder.initExecutor(options());

    ThreadPoolExecutor executor = holder.getExecutor();

    assertThat(executor).isNotNull();
    assertThat(executor.getCorePoolSize()).isEqualTo(1);
    assertThat(executor.getMaximumPoolSize()).isEqualTo(2);
    assertThat(executor.getKeepAliveTime(TimeUnit.SECONDS)).isEqualTo(60);
    assertThat(executor.getQueue()).isInstanceOf(ArrayBlockingQueue.class);
    assertThat(executor.getQueue().remainingCapacity()).isEqualTo(2);
    assertThat(executor.getThreadFactory()).isInstanceOf(SimpleNamedThreadFactory.class);
  }

  @Test
  @DisplayName("配置为空时回退默认值：核心数为 CPU 数 + 1，队列容量 1000")
  void shouldApplyDefaultsForUnsetOptions() {
    holder.initExecutor(new ThreadExecutionOptions());

    ThreadPoolExecutor executor = holder.getExecutor();
    int expectedCore = Runtime.getRuntime().availableProcessors() + 1;

    assertThat(executor.getCorePoolSize()).isEqualTo(expectedCore);
    assertThat(executor.getMaximumPoolSize()).isEqualTo(expectedCore * 2);
    assertThat(executor.getQueue().remainingCapacity()).isEqualTo(1000);
    assertThat(executor.getKeepAliveTime(TimeUnit.HOURS)).isEqualTo(1);
  }

  @Test
  @DisplayName("工作线程使用配置的名称前缀与守护线程标记")
  void shouldCreateThreadsWithConfiguredNameAndDaemonFlag() throws InterruptedException {
    holder.initExecutor(options());

    AtomicReference<String> threadName = new AtomicReference<>();
    AtomicReference<Boolean> daemon = new AtomicReference<>();
    CountDownLatch done = new CountDownLatch(1);

    holder
        .getExecutor()
        .execute(
            () -> {
              threadName.set(Thread.currentThread().getName());
              daemon.set(Thread.currentThread().isDaemon());
              done.countDown();
            });

    assertThat(done.await(5, TimeUnit.SECONDS)).isTrue();
    assertThat(threadName.get()).isEqualTo("test-pool--");
    assertThat(daemon.get()).isTrue();
  }

  @Test
  @DisplayName("重复初始化不会改变已创建的线程池")
  void shouldIgnoreLaterInitCalls() {
    holder.initExecutor(options());
    ThreadPoolExecutor first = holder.getExecutor();

    ThreadExecutionOptions other = options();
    other.setCorePoolSize(8);
    other.setThreadNamePrefix("other-");
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
    Set<ThreadPoolExecutor> executors = ConcurrentHashMap.newKeySet();

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
  @DisplayName("队列与最大线程数都占满时任务被拒绝并抛出 RejectedExecutionException")
  void shouldRejectTaskWhenPoolIsSaturated() throws InterruptedException {
    holder.initExecutor(smallPoolOptions());
    ThreadPoolExecutor executor = holder.getExecutor();

    CountDownLatch running = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);
    try {
      executor.execute(
          () -> {
            running.countDown();
            try {
              release.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          });
      assertThat(running.await(5, TimeUnit.SECONDS)).isTrue();

      executor.execute(() -> {}); // 进入队列

      assertThatThrownBy(() -> executor.execute(() -> {}))
          .isInstanceOf(RejectedExecutionException.class)
          .hasMessageContaining("rejected from");
      assertThat(executor.getQueue().size()).isEqualTo(1);
    } finally {
      release.countDown();
    }
  }

  @Test
  @DisplayName("awaitTermination=true 时优雅关闭并等待线程池终结")
  void shouldShutdownAndAwaitTermination() {
    holder.initExecutor(options());
    ThreadPoolExecutor executor = holder.getExecutor();

    holder.shutdown();

    assertThat(executor.isShutdown()).isTrue();
    assertThat(executor.isTerminated()).isTrue();
  }

  @Test
  @DisplayName("awaitTermination=false 时直接强制关闭")
  void shouldForceShutdownWhenAwaitTerminationDisabled() {
    ThreadExecutionOptions options = options();
    options.setAwaitTermination(false);
    holder.initExecutor(options);
    ThreadPoolExecutor executor = holder.getExecutor();

    holder.shutdown();

    assertThat(executor.isShutdown()).isTrue();
    assertThat(executor.isTerminated()).isTrue();
  }

  @Test
  @DisplayName("现状记录：allowCoreThreadTimeOut 配置当前不会传递到线程池")
  void shouldNotApplyAllowCoreThreadTimeOut() {
    ThreadExecutionOptions options = options();
    options.setAllowCoreThreadTimeOut(true);

    holder.initExecutor(options);

    assertThat(holder.getExecutor().allowsCoreThreadTimeOut()).isFalse();
  }
}
