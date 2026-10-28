package tutorials4j.toolkit.redisson.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * {@link RedissonReentrantLockableAspect} 单元测试：等待时间与过期时间的解析及锁路径选择。
 *
 * @author YunJiao Yang
 */
class RedissonReentrantLockableAspectTest {
  private final RedissonReentrantLockService lockService = mock(RedissonReentrantLockService.class);
  private final RedissonReentrantLockService.FixedLease fixedLease =
      mock(RedissonReentrantLockService.FixedLease.class);
  private final RedissonReentrantLockService.AutoRenewal autoRenewal =
      mock(RedissonReentrantLockService.AutoRenewal.class);
  private final SpelMethodBasedExpressionEvaluator expressionEvaluator =
      new SpelMethodBasedExpressionEvaluator();

  private final RedissonReentrantLockableAspect aspect =
      new RedissonReentrantLockableAspect(expressionEvaluator, lockService);

  @BeforeEach
  void setUp() {
    expressionEvaluator.setEmbeddedValueResolver(value -> value);
    when(lockService.fixedLease()).thenReturn(fixedLease);
    when(lockService.autoRenewal()).thenReturn(autoRenewal);
  }

  /** doInLock 同时存在 Runnable 与 ThrowingCallable 两个重载，必须使用带类型的匹配器。 */
  @SuppressWarnings("unchecked")
  private static <T> ThrowingCallable<T> anyCallable() {
    return (ThrowingCallable<T>) ArgumentMatchers.any(ThrowingCallable.class);
  }

  @RedissonReentrantLockable(key = "'user:' + #id")
  public String autoRenewalMethod(Long id) {
    return "auto:" + id;
  }

  @RedissonReentrantLockable(
      prefix = "biz:",
      key = "#id",
      waitTime = 2,
      timeUnit = TimeUnit.SECONDS,
      expireTime = 5)
  public String fixedLeaseMethod(Long id) {
    return "fixed:" + id;
  }

  private static ProceedingJoinPoint joinPointOf(String methodName, Object... args)
      throws NoSuchMethodException {
    Method method = RedissonReentrantLockableAspectTest.class.getMethod(methodName, Long.class);
    MethodSignature signature = mock(MethodSignature.class);
    when(signature.getMethod()).thenReturn(method);

    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(signature);
    when(joinPoint.getArgs()).thenReturn(args);
    return joinPoint;
  }

  private static RedissonReentrantLockable annotationOf(String methodName)
      throws NoSuchMethodException {
    return RedissonReentrantLockableAspectTest.class
        .getMethod(methodName, Long.class)
        .getAnnotation(RedissonReentrantLockable.class);
  }

  @Test
  @DisplayName("未配置过期时间时使用看门狗续期锁，并透传默认等待时间 3 秒")
  void shouldUseAutoRenewalWithDefaultWaitTime() throws Throwable {
    String key = DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "user:7";
    ProceedingJoinPoint joinPoint = joinPointOf("autoRenewalMethod", 7L);
    when(autoRenewal.doInLock(eq(key), eq(Duration.ofSeconds(3)), anyCallable()))
        .thenReturn("result");

    Object result = aspect.around(joinPoint, annotationOf("autoRenewalMethod"));

    assertThat(result).isEqualTo("result");
    verify(autoRenewal).doInLock(eq(key), eq(Duration.ofSeconds(3)), anyCallable());
    verify(lockService, never()).fixedLease();
  }

  @Test
  @DisplayName("配置过期时间时使用固定租期锁，并按 timeUnit 换算等待时间与过期时间")
  void shouldUseFixedLeaseWithWaitAndExpireTime() throws Throwable {
    String key = DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "biz:7";
    ProceedingJoinPoint joinPoint = joinPointOf("fixedLeaseMethod", 7L);
    when(fixedLease.doInLock(
            eq(key), eq(Duration.ofSeconds(2)), eq(Duration.ofSeconds(5)), anyCallable()))
        .thenReturn("result");

    Object result = aspect.around(joinPoint, annotationOf("fixedLeaseMethod"));

    assertThat(result).isEqualTo("result");
    verify(fixedLease)
        .doInLock(eq(key), eq(Duration.ofSeconds(2)), eq(Duration.ofSeconds(5)), anyCallable());
    verify(lockService, never()).autoRenewal();
  }

  @Test
  @DisplayName("把被拦截的方法与参数交给 SpEL 求值器")
  void shouldPassMethodAndArgumentsToExpressionEvaluator() throws Throwable {
    SpelMethodBasedExpressionEvaluator evaluator = mock(SpelMethodBasedExpressionEvaluator.class);
    RedissonReentrantLockableAspect mockedAspect =
        new RedissonReentrantLockableAspect(evaluator, lockService);
    Method method =
        RedissonReentrantLockableAspectTest.class.getMethod("autoRenewalMethod", Long.class);
    ProceedingJoinPoint joinPoint = joinPointOf("autoRenewalMethod", 7L);
    Object[] args = joinPoint.getArgs();
    RedissonReentrantLockable annotation = annotationOf("autoRenewalMethod");
    when(evaluator.getValue(method, args, annotation.key(), String.class)).thenReturn("value");
    when(autoRenewal.doInLock(
            eq(DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "value"),
            any(Duration.class),
            anyCallable()))
        .thenReturn("result");

    Object result = mockedAspect.around(joinPoint, annotation);

    assertThat(result).isEqualTo("result");
    verify(evaluator).getValue(eq(method), eq(args), eq("'user:' + #id"), eq(String.class));
  }

  @Test
  @DisplayName("业务方法结果通过 ThrowingCallable 透传")
  void shouldReturnResultOfProceed() throws Throwable {
    ProceedingJoinPoint joinPoint = joinPointOf("fixedLeaseMethod", 3L);
    when(joinPoint.proceed()).thenReturn("proceeded");
    when(fixedLease.doInLock(
            eq(DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "biz:3"),
            any(Duration.class),
            any(Duration.class),
            anyCallable()))
        .thenAnswer(invocation -> ((ThrowingCallable<?>) invocation.getArgument(3)).call());

    Object result = aspect.around(joinPoint, annotationOf("fixedLeaseMethod"));

    assertThat(result).isEqualTo("proceeded");
    verify(joinPoint).proceed();
  }

  @Test
  @DisplayName("业务方法抛出的异常向外传播")
  void shouldPropagateExceptionFromProceed() throws Throwable {
    ProceedingJoinPoint joinPoint = joinPointOf("autoRenewalMethod", 3L);
    when(joinPoint.proceed()).thenThrow(new IllegalStateException("boom"));
    when(autoRenewal.doInLock(
            eq(DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "user:3"),
            any(Duration.class),
            anyCallable()))
        .thenAnswer(invocation -> ((ThrowingCallable<?>) invocation.getArgument(2)).call());

    assertThatThrownBy(() -> aspect.around(joinPoint, annotationOf("autoRenewalMethod")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("boom");
  }
}
