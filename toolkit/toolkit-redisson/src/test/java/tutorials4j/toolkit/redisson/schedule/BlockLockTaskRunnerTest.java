package tutorials4j.toolkit.redisson.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.redisson.lock.RedissonLockServiceTestFixture;

/**
 * 阻塞式锁任务运行器测试：{@link AutoRenewalBlockLockTaskRunner} 与 {@link FixedLeaseBlockLockTaskRunner}。
 *
 * @author YunJiao Yang
 */
class BlockLockTaskRunnerTest {
  private static final String LOCK_KEY = DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "job:1";

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

  static class AutoRenewalRunner implements AutoRenewalBlockLockTaskRunner {
    final List<Map<String, String>> executions = new ArrayList<>();
    boolean failing;

    @Override
    public String key(Map<String, String> params) {
      return "job:" + params.get("id");
    }

    @Override
    public void doRun(Map<String, String> params) {
      if (failing) {
        throw new IllegalStateException("boom");
      }
      executions.add(params);
    }
  }

  static class FixedLeaseRunner implements FixedLeaseBlockLockTaskRunner {
    final List<Map<String, String>> executions = new ArrayList<>();
    boolean failing;

    @Override
    public String key(Map<String, String> params) {
      return "job:" + params.get("id");
    }

    @Override
    public Duration expireTime() {
      return Duration.ofMinutes(1);
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
  @DisplayName("看门狗续期运行器：使用无租期 lock() 执行任务并释放锁")
  void shouldRunTaskUnderWatchdogLock() {
    AutoRenewalRunner runner = new AutoRenewalRunner();

    runner.run(Map.of("id", "1"));

    assertThat(runner.executions).containsExactly(Map.of("id", "1"));
    verify(lock).lock();
    verify(lock, never()).lock(anyLong(), any(TimeUnit.class));
    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期运行器：按 expireTime() 加锁执行任务并释放锁")
  void shouldRunTaskUnderFixedLeaseLock() {
    FixedLeaseRunner runner = new FixedLeaseRunner();

    runner.run(Map.of("id", "1"));

    assertThat(runner.executions).containsExactly(Map.of("id", "1"));
    verify(lock).lock(60_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("任务异常被捕获记录，且锁仍被释放")
  void shouldSwallowTaskFailureAndReleaseLock() {
    AutoRenewalRunner autoRenewalRunner = new AutoRenewalRunner();
    autoRenewalRunner.failing = true;
    FixedLeaseRunner fixedLeaseRunner = new FixedLeaseRunner();
    fixedLeaseRunner.failing = true;

    assertThatCode(() -> autoRenewalRunner.run(Map.of("id", "1"))).doesNotThrowAnyException();
    assertThatCode(() -> fixedLeaseRunner.run(Map.of("id", "1"))).doesNotThrowAnyException();

    assertThat(autoRenewalRunner.executions).isEmpty();
    assertThat(fixedLeaseRunner.executions).isEmpty();
    verify(lock, times(2)).unlock();
  }

  @Test
  @DisplayName("默认锁属性：前缀，等待时间为 null")
  void shouldExposeDefaultLockableAttributes() {
    AutoRenewalRunner autoRenewalRunner = new AutoRenewalRunner();
    FixedLeaseRunner fixedLeaseRunner = new FixedLeaseRunner();

    assertThat(autoRenewalRunner.prefix()).isEqualTo("");
    assertThat(autoRenewalRunner.waitTime()).isNull();
    assertThat(autoRenewalRunner.expireTime()).isNull();

    assertThat(fixedLeaseRunner.prefix()).isEqualTo("");
    assertThat(fixedLeaseRunner.waitTime()).isNull();
    assertThat(fixedLeaseRunner.expireTime()).isEqualTo(Duration.ofMinutes(1));

    assertThat(autoRenewalRunner.key(Map.of("id", "9"))).isEqualTo("job:9");
    assertThat(fixedLeaseRunner.key(Map.of("id", "9"))).isEqualTo("job:9");
  }
}
