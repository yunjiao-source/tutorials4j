package tutorials4j.toolkit.redisson.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * {@link RedissonReentrantLockService} 单元测试：带等待时间的可重入锁。
 *
 * @author YunJiao Yang
 */
class RedissonReentrantLockServiceTest {
  private static final String LOCK_KEY = "job:1";

  private final RedissonReentrantLockService lockService = RedissonReentrantLockService.instance;
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
    Thread.interrupted();
  }

  @Test
  @DisplayName("默认等待时间为 3 秒")
  void shouldExposeDefaultWaitTime() {
    assertThat(RedissonReentrantLockService.WAIT_SECONDS).isEqualTo(3);
  }

  @Test
  @DisplayName("固定租期锁：按等待时间与过期时间 tryLock、执行任务并释放锁")
  void shouldTryLockWithWaitAndExpireTime() throws InterruptedException {
    when(lock.tryLock(1_000L, 2_000L, TimeUnit.MILLISECONDS)).thenReturn(true);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task);

    assertThat(executed).isTrue();
    verify(lock).tryLock(1_000L, 2_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期锁：三参重载使用默认等待时间 3 秒")
  void shouldUseDefaultWaitTimeForThreeArgOverload() throws InterruptedException {
    when(lock.tryLock(3_000L, 5_000L, TimeUnit.MILLISECONDS)).thenReturn(true);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(5), task);

    verify(lock).tryLock(3_000L, 5_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期锁：未获取到锁抛出 ACQUIRE_LOCK_FAIL 并携带参数")
  void shouldThrowWhenFixedLeaseLockNotAcquired() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    assertThatThrownBy(
            () ->
                lockService
                    .fixedLease()
                    .doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("获取锁失败")
        .satisfies(
            throwable -> {
              ErrorCodeException exception = (ErrorCodeException) throwable;
              assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.ACQUIRE_LOCK_FAIL);
              assertThat(exception.getParams())
                  .containsEntry("lockKey", LOCK_KEY)
                  .containsEntry("waitTime", Duration.ofSeconds(1))
                  .containsEntry("expireTime", Duration.ofSeconds(2));
            });

    assertThat(executed).isFalse();
    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("固定租期锁：等待过程被中断时恢复中断标记并抛出系统异常")
  void shouldThrowSystemExceptionWhenInterrupted() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class)))
        .thenThrow(new InterruptedException("interrupted"));
    Runnable task = () -> {};

    assertThatThrownBy(
            () ->
                lockService
                    .fixedLease()
                    .doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("系统异常")
        .satisfies(
            throwable ->
                assertThat(((ErrorCodeException) throwable).getErrorCode())
                    .isEqualTo(CommonErrorCode.SYSTEM_EXCEPTION));

    assertThat(Thread.currentThread().isInterrupted()).isTrue();
  }

  @Test
  @DisplayName("固定租期锁：透传返回值型任务的结果")
  void shouldReturnCallableResultWithinFixedLease() throws Throwable {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    ThrowingCallable<String> callable = () -> "done";

    String result =
        lockService
            .fixedLease()
            .doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), callable);

    assertThat(result).isEqualTo("done");
    verify(lock).unlock();
  }

  @Test
  @DisplayName("固定租期锁：任务异常仍释放锁")
  void shouldUnlockWhenFixedLeaseTaskFails() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    Runnable task =
        () -> {
          throw new IllegalStateException("boom");
        };

    assertThatThrownBy(
            () ->
                lockService
                    .fixedLease()
                    .doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task))
        .isInstanceOf(IllegalStateException.class);

    verify(lock).unlock();
  }

  @Test
  @DisplayName("自动续期锁：按等待时间 tryLock（无租期），执行任务并释放锁")
  void shouldTryLockWithWaitTimeForAutoRenewal() throws InterruptedException {
    when(lock.tryLock(1_000L, TimeUnit.MILLISECONDS)).thenReturn(true);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    lockService.autoRenewal().doInLock(LOCK_KEY, Duration.ofSeconds(1), task);

    assertThat(executed).isTrue();
    verify(lock).tryLock(1_000L, TimeUnit.MILLISECONDS);
    verify(lock, never()).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
    verify(lock).unlock();
  }

  @Test
  @DisplayName("自动续期锁：两参重载使用默认等待时间 3 秒")
  void shouldUseDefaultWaitTimeForAutoRenewalOverload() throws Throwable {
    when(lock.tryLock(3_000L, TimeUnit.MILLISECONDS)).thenReturn(true);
    ThrowingCallable<String> callable = () -> "renewed";

    String result = lockService.autoRenewal().doInLock(LOCK_KEY, callable);

    assertThat(result).isEqualTo("renewed");
    verify(lock).tryLock(3_000L, TimeUnit.MILLISECONDS);
  }

  @Test
  @DisplayName("自动续期锁：未获取到锁抛出 ACQUIRE_LOCK_FAIL")
  void shouldThrowWhenAutoRenewalLockNotAcquired() throws InterruptedException {
    when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(false);
    Runnable task = () -> {};

    assertThatThrownBy(
            () -> lockService.autoRenewal().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("获取锁失败")
        .satisfies(
            throwable -> {
              ErrorCodeException exception = (ErrorCodeException) throwable;
              assertThat(exception.getParams())
                  .containsEntry("lockKey", LOCK_KEY)
                  .containsEntry("waitTime", Duration.ofSeconds(1));
            });

    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("自动续期锁：等待过程被中断时恢复中断标记并抛出系统异常")
  void shouldThrowSystemExceptionWhenAutoRenewalInterrupted() throws InterruptedException {
    when(lock.tryLock(anyLong(), any(TimeUnit.class)))
        .thenThrow(new InterruptedException("interrupted"));
    Runnable task = () -> {};

    assertThatThrownBy(
            () -> lockService.autoRenewal().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("系统异常");

    assertThat(Thread.currentThread().isInterrupted()).isTrue();
  }

  @Test
  @DisplayName("解锁异常时，不抛出异常，只记录日志")
  void shouldWrapUnlockFailure() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    doThrow(new IllegalStateException("unlock failed")).when(lock).unlock();
    Runnable task = () -> {};

    assertThatCode(
            () ->
                lockService
                    .fixedLease()
                    .doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("当前线程未持有锁时不执行解锁")
  void shouldNotUnlockWhenNotHeldByCurrentThread() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    when(lock.isHeldByCurrentThread()).thenReturn(false);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task);

    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("锁未处于锁定状态时不执行解锁")
  void shouldNotUnlockWhenNotLocked() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    when(lock.isLocked()).thenReturn(false);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task);

    verify(lock, never()).unlock();
  }

  @Test
  @DisplayName("锁名通过 redissonClient.getLock 按 key 获取")
  void shouldResolveLockByKey() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task);

    verify(redissonClient).getLock(eq(LOCK_KEY));
  }
}
