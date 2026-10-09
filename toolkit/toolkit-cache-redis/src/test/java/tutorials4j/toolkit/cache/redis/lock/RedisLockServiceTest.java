package tutorials4j.toolkit.cache.redis.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tutorials4j.toolkit.cache.redis.script.RedisScriptExecutor;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.exception.ErrorCodeException;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * {@link RedisLockService} 单元测试：固定租期锁与自动续期锁的加锁/释放流程。
 *
 * @author Yun Jiao
 */
class RedisLockServiceTest {
  private static final String LOCK_KEY = "job:1";
  private static final long AUTO_RENEWAL_EXPIRE_MILLIS = 30_000L;

  private final RedisLockService lockService = RedisLockService.instance;
  private final RedisScriptExecutor scriptExecutor = mock(RedisScriptExecutor.class);

  @BeforeEach
  void setUp() {
    RedisLockServiceTestFixture.useScriptExecutor(scriptExecutor);
  }

  @AfterEach
  void tearDown() {
    RedisLockServiceTestFixture.resetLocks();
  }

  @Test
  @DisplayName("fixedLease 与 autoRenewal 分别是单例式的延迟加载对象")
  void shouldReuseInnerLockInstances() {
    assertThat(lockService.fixedLease()).isSameAs(lockService.fixedLease());
    assertThat(lockService.autoRenewal()).isSameAs(lockService.autoRenewal());
    assertThat(lockService.fixedLease()).isNotSameAs(lockService.autoRenewal());
  }

  @Test
  @DisplayName("并发访问 fixedLease 只创建一个内部对象")
  void shouldCreateSingleFixedLeaseConcurrently() throws InterruptedException {
    int threads = 8;
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(threads);
    Set<RedisLockService.FixedLease> instances = ConcurrentHashMap.newKeySet();

    for (int i = 0; i < threads; i++) {
      new Thread(
              () -> {
                try {
                  start.await();
                  instances.add(lockService.fixedLease());
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                } finally {
                  finished.countDown();
                }
              })
          .start();
    }

    start.countDown();
    assertThat(finished.await(5, TimeUnit.SECONDS)).isTrue();

    assertThat(instances).hasSize(1);
  }

  @Test
  @DisplayName("固定租期锁：加锁成功、执行任务并按毫秒过期时间释放锁")
  void shouldAcquireRunAndReleaseFixedLeaseLock() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(5_000L))).thenReturn(true);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(5), task);

    assertThat(executed).isTrue();

    ArgumentCaptor<String> lockId = ArgumentCaptor.forClass(String.class);
    verify(scriptExecutor).setIfAbsent(eq(LOCK_KEY), lockId.capture(), eq(5_000L));
    assertThat(lockId.getValue()).isNotBlank();
    verify(scriptExecutor).deleteIfSame(LOCK_KEY, lockId.getValue());
  }

  @Test
  @DisplayName("固定租期锁：加锁失败抛出 ACQUIRE_LOCK_FAIL 并携带参数")
  void shouldThrowWhenFixedLeaseLockNotAcquired() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_000L))).thenReturn(false);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    assertThatThrownBy(
            () -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("获取锁失败")
        .satisfies(
            throwable -> {
              ErrorCodeException exception = (ErrorCodeException) throwable;
              assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.ACQUIRE_LOCK_FAIL);
              assertThat(exception.getParams())
                  .containsEntry("lockKey", LOCK_KEY)
                  .containsEntry("expireTime", Duration.ofSeconds(1));
            });

    assertThat(executed).isFalse();
    verify(scriptExecutor, never()).deleteIfSame(anyString(), anyString());
  }

  @Test
  @DisplayName("固定租期锁：任务抛异常仍会释放锁并向外抛出")
  void shouldReleaseFixedLeaseLockWhenTaskFails() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_000L))).thenReturn(true);
    Runnable task =
        () -> {
          throw new IllegalStateException("boom");
        };

    assertThatThrownBy(
            () -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("boom");

    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("固定租期锁：返回值型任务的结果被透传")
  void shouldReturnCallableResultWithinFixedLeaseLock() throws Throwable {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_000L))).thenReturn(true);
    ThrowingCallable<String> callable = () -> "done";

    String result = lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), callable);

    assertThat(result).isEqualTo("done");
    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("固定租期锁：返回值型任务抛异常时同样释放锁")
  void shouldReleaseFixedLeaseLockWhenCallableFails() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_000L))).thenReturn(true);
    ThrowingCallable<String> callable =
        () -> {
          throw new IllegalStateException("boom");
        };

    assertThatThrownBy(
            () -> lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), callable))
        .isInstanceOf(IllegalStateException.class);

    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("锁已被提前释放时释放失败不抛异常")
  void shouldTolerateAlreadyReleasedLock() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_000L))).thenReturn(true);
    when(scriptExecutor.deleteIfSame(eq(LOCK_KEY), anyString())).thenReturn(false);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task);

    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("释放锁时使用与加锁相同的 lockId")
  void shouldReleaseWithSameLockId() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_000L))).thenReturn(true);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofSeconds(1), task);

    ArgumentCaptor<String> acquired = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> released = ArgumentCaptor.forClass(String.class);
    verify(scriptExecutor).setIfAbsent(eq(LOCK_KEY), acquired.capture(), anyLong());
    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), released.capture());

    assertThat(released.getValue()).isEqualTo(acquired.getValue());
  }

  @Test
  @DisplayName("自动续期锁：加锁成功、执行任务并按默认 30 秒过期时间释放锁")
  void shouldAcquireRunAndReleaseAutoRenewalLock() {
    RedisLockServiceTestFixture.initScheduler();
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(AUTO_RENEWAL_EXPIRE_MILLIS)))
        .thenReturn(true);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    lockService.autoRenewal().doInLock(LOCK_KEY, task);

    assertThat(executed).isTrue();

    ArgumentCaptor<String> lockId = ArgumentCaptor.forClass(String.class);
    verify(scriptExecutor)
        .setIfAbsent(eq(LOCK_KEY), lockId.capture(), eq(AUTO_RENEWAL_EXPIRE_MILLIS));
    verify(scriptExecutor).deleteIfSame(LOCK_KEY, lockId.getValue());
  }

  @Test
  @DisplayName("自动续期锁：加锁失败抛出 ACQUIRE_LOCK_FAIL")
  void shouldThrowWhenAutoRenewalLockNotAcquired() {
    RedisLockServiceTestFixture.initScheduler();
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(AUTO_RENEWAL_EXPIRE_MILLIS)))
        .thenReturn(false);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    assertThatThrownBy(() -> lockService.autoRenewal().doInLock(LOCK_KEY, task))
        .isInstanceOf(ErrorCodeException.class)
        .hasMessage("获取锁失败");

    assertThat(executed).isFalse();
    verify(scriptExecutor, never()).deleteIfSame(anyString(), anyString());
  }

  @Test
  @DisplayName("自动续期锁：返回值型任务的结果被透传")
  void shouldReturnCallableResultWithinAutoRenewalLock() throws Throwable {
    RedisLockServiceTestFixture.initScheduler();
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(AUTO_RENEWAL_EXPIRE_MILLIS)))
        .thenReturn(true);
    ThrowingCallable<String> callable = () -> "renewed";

    String result = lockService.autoRenewal().doInLock(LOCK_KEY, callable);

    assertThat(result).isEqualTo("renewed");
    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("现状记录：未初始化计划任务线程池时自动续期锁抛 NullPointerException")
  void shouldRequireSchedulerForAutoRenewal() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(AUTO_RENEWAL_EXPIRE_MILLIS)))
        .thenReturn(true);
    Runnable task = () -> {};
    RedisLockServiceTestFixture.resetScheduler();
    try {
      assertThatThrownBy(() -> lockService.autoRenewal().doInLock(LOCK_KEY, task))
          .isInstanceOf(NullPointerException.class);
    } finally {
      RedisLockServiceTestFixture.initScheduler();
    }
  }

  @Test
  @DisplayName("锁超时时间以毫秒传给 setIfAbsent")
  void shouldPassExpireTimeInMillis() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), anyLong())).thenReturn(true);
    Runnable task = () -> {};

    lockService.fixedLease().doInLock(LOCK_KEY, Duration.ofMillis(1500), task);

    verify(scriptExecutor).setIfAbsent(eq(LOCK_KEY), anyString(), eq(1_500L));
    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
    verify(scriptExecutor, never()).checkAndResetExpire(anyString(), anyString(), any(Long.class));
  }
}
