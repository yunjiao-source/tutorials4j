package tutorials4j.toolkit.redisson.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * {@link RedissonBlockLockService} 单元测试：阻塞式固定租期锁与看门狗续期锁。
 *
 * @author YunJiao Yang
 */
class RedissonBlockLockServiceTest {
  private static final String LOCK_KEY = "job:1";

  private final RedissonBlockLockService lockService = RedissonBlockLockService.instance;
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

  @Test
  @DisplayName("fixedLease 与 autoRenewal 分别是单例式的延迟加载对象")
  void shouldReuseInnerLockInstances() {
    assertThat(lockService.fixedLease()).isSameAs(lockService.fixedLease());
    assertThat(lockService.autoRenewal()).isSameAs(lockService.autoRenewal());
    assertThat(lockService.fixedLease()).isNotSameAs(lockService.autoRenewal());
  }

  @Test
  @DisplayName("固定租期锁：按过期时间加锁、执行任务并释放锁")
  void shouldLockWithExpireTimeAndRunTask() {
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(5), task);

    assertThat(executed).isTrue();
    verify(redissonClient).getLock(LOCK_KEY);
    verify(lock).lock(5_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期锁：透传返回值型任务的结果")
  void shouldReturnCallableResultWithinFixedLease() throws Throwable {
    ThrowingCallable<String> callable = () -> "done";

    String result = lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), callable);

    assertThat(result).isEqualTo("done");
    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期锁：任务异常仍释放锁并向外抛出")
  void shouldUnlockWhenTaskFails() {
    Runnable task =
        () -> {
          throw new IllegalStateException("boom");
        };

    assertThatThrownBy(
            () -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("boom");

    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期锁：返回值型任务异常同样释放锁")
  void shouldUnlockWhenCallableFails() {
    ThrowingCallable<String> callable =
        () -> {
          throw new IllegalStateException("boom");
        };

    assertThatThrownBy(
            () -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), callable))
        .isInstanceOf(IllegalStateException.class);

    verify(lock).unlock();
  }

  @Test
  @DisplayName("当前线程未持有锁时不执行解锁")
  void shouldNotUnlockWhenNotHeldByCurrentThread() {
    when(lock.isHeldByCurrentThread()).thenReturn(false);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task);

    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("锁已不再锁定状态时不执行解锁")
  void shouldNotUnlockWhenNotLocked() {
    when(lock.isLocked()).thenReturn(false);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task);

    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("解锁异常时，不抛出异常，只记录日志")
  void shouldNotThrowWhenUnlockFailure() {
    doThrow(new IllegalStateException("unlock failed")).when(lock).unlock();
    Runnable task = () -> {};

    assertThatCode(() -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("解锁失败不会覆盖业务异常")
  void shouldMaskTaskExceptionWhenUnlockFails() {
    doThrow(new IllegalStateException("unlock failed")).when(lock).unlock();
    Runnable task =
        () -> {
          throw new IllegalArgumentException("business failure");
        };

    assertThatThrownBy(
            () -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("business failure");
  }

  @Test
  @DisplayName("自动续期锁：使用无租期的 lock() 加锁并释放")
  void shouldUseWatchdogLockForAutoRenewal() {
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    lockService.autoRenewal().doInLock(LOCK_KEY, task);

    assertThat(executed).isTrue();
    verify(lock).lock();
    verify(lock, never()).lock(anyLong(), any(TimeUnit.class));
    verify(lock).unlock();
  }

  @Test
  @DisplayName("自动续期锁：透传返回值型任务的结果")
  void shouldReturnCallableResultWithinAutoRenewal() throws Throwable {
    ThrowingCallable<String> callable = () -> "renewed";

    String result = lockService.autoRenewal().doInLock(LOCK_KEY, callable);

    assertThat(result).isEqualTo("renewed");
    verify(lock).unlock();
  }

  @Test
  @DisplayName("自动续期锁：任务异常仍释放锁")
  void shouldUnlockWhenAutoRenewalTaskFails() {
    Runnable task =
        () -> {
          throw new IllegalStateException("boom");
        };

    assertThatThrownBy(() -> lockService.autoRenewal().doInLock(LOCK_KEY, task))
        .isInstanceOf(IllegalStateException.class);

    verify(lock).unlock();
  }
}
