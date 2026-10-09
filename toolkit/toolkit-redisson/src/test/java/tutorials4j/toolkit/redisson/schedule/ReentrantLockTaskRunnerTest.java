package tutorials4j.toolkit.redisson.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.redisson.lock.RedissonLockServiceTestFixture;

/**
 * 带等待时间的可重入锁任务运行器测试：{@link AutoRenewalReentrantLockTaskRunner} 与 {@link
 * FixedLeaseReentrantLockTaskRunner}。
 *
 * @author Yun Jiao
 */
class ReentrantLockTaskRunnerTest {
  private static final String LOCK_KEY = ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "job:1";

  private final RedissonClient redissonClient = mock(RedissonClient.class);
  private final RLock lock = mock(RLock.class);

  @BeforeEach
  void setUp() {
    RedissonLockServiceTestFixture.useRedissonClient(redissonClient);
    when(redissonClient.getLock(LOCK_KEY)).thenReturn(lock);
    when(lock.getName()).thenReturn(LOCK_KEY);
    when(lock.isHeldByCurrentThread()).thenReturn(true);
    when(lock.isLocked()).thenReturn(true);
  }

  @AfterEach
  void tearDown() {
    RedissonLockServiceTestFixture.resetLocks();
  }

  static class AutoRenewalRunner implements AutoRenewalReentrantLockTaskRunner {
    final List<Map<String, String>> executions = new ArrayList<>();
    boolean failing;

    @Override
    public String key(Map<String, String> params) {
      return "job:" + params.get("id");
    }

    @Override
    public Duration waitTime() {
      return Duration.ofSeconds(1);
    }

    @Override
    public void doRun(Map<String, String> params) {
      if (failing) {
        throw new IllegalStateException("boom");
      }
      executions.add(params);
    }
  }

  static class FixedLeaseRunner implements FixedLeaseReentrantLockTaskRunner {
    final List<Map<String, String>> executions = new ArrayList<>();
    boolean failing;

    @Override
    public String key(Map<String, String> params) {
      return "job:" + params.get("id");
    }

    @Override
    public Duration waitTime() {
      return Duration.ofSeconds(1);
    }

    @Override
    public Duration expireTime() {
      return Duration.ofMinutes(5);
    }

    @Override
    public void doRun(Map<String, String> params) {
      if (failing) {
        throw new IllegalStateException("boom");
      }
      executions.add(params);
    }
  }

  @Test
  @DisplayName("自动续期运行器：按 waitTime() 等待获取锁后执行任务并释放锁")
  void shouldRunTaskUnderReentrantAutoRenewal() throws InterruptedException {
    when(lock.tryLock(1_000L, TimeUnit.MILLISECONDS)).thenReturn(true);
    AutoRenewalRunner runner = new AutoRenewalRunner();

    runner.run(Map.of("id", "1"));

    assertThat(runner.executions).containsExactly(Map.of("id", "1"));
    verify(lock).tryLock(1_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("现状记录：固定租期运行器把 waitTime() 同时当作过期时间，声明的 expireTime() 未被使用")
  void shouldUseWaitTimeAsExpireTimeForReentrantFixedLease() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    FixedLeaseRunner runner = new FixedLeaseRunner();

    runner.run(Map.of("id", "1"));

    assertThat(runner.executions).containsExactly(Map.of("id", "1"));
    verify(lock).tryLock(1_000L, 300_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("未获取到锁时静默跳过任务")
  void shouldSkipTaskWhenLockNotAcquired() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);
    when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(false);
    FixedLeaseRunner fixedLeaseRunner = new FixedLeaseRunner();
    AutoRenewalRunner autoRenewalRunner = new AutoRenewalRunner();

    assertThatCode(() -> fixedLeaseRunner.run(Map.of("id", "1"))).doesNotThrowAnyException();
    assertThatCode(() -> autoRenewalRunner.run(Map.of("id", "1"))).doesNotThrowAnyException();

    assertThat(fixedLeaseRunner.executions).isEmpty();
    assertThat(autoRenewalRunner.executions).isEmpty();
    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("任务异常被捕获记录，且锁仍被释放")
  void shouldSwallowTaskFailureAndReleaseLock() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    FixedLeaseRunner runner = new FixedLeaseRunner();
    runner.failing = true;

    assertThatCode(() -> runner.run(Map.of("id", "1"))).doesNotThrowAnyException();

    assertThat(runner.executions).isEmpty();
    verify(lock).unlock();
  }

  @Test
  @DisplayName("默认锁属性：前缀，自动续期运行器的过期时间为 null")
  void shouldExposeDefaultLockableAttributes() {
    AutoRenewalRunner autoRenewalRunner = new AutoRenewalRunner();
    FixedLeaseRunner fixedLeaseRunner = new FixedLeaseRunner();

    assertThat(autoRenewalRunner.prefix()).isEqualTo("");
    assertThat(autoRenewalRunner.expireTime()).isNull();
    assertThat(autoRenewalRunner.waitTime()).isEqualTo(Duration.ofSeconds(1));

    assertThat(fixedLeaseRunner.expireTime()).isEqualTo(Duration.ofMinutes(5));
    assertThat(fixedLeaseRunner.key(Map.of("id", "9"))).isEqualTo("job:9");
  }
}
