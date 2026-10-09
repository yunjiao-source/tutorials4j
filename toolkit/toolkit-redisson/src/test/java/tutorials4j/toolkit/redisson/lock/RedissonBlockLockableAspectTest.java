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
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * {@link RedissonBlockLockableAspect} 单元测试：注解解析、密钥生成与锁路径选择（阻塞式）。
 *
 * @author Yun Jiao
 */
class RedissonBlockLockableAspectTest {
  private final RedissonBlockLockService lockService = mock(RedissonBlockLockService.class);
  private final RedissonBlockLockService.FixedLease fixedLease =
      mock(RedissonBlockLockService.FixedLease.class);
  private final RedissonBlockLockService.AutoRenewal autoRenewal =
      mock(RedissonBlockLockService.AutoRenewal.class);
  private final SpelMethodBasedExpressionEvaluator expressionEvaluator =
      new SpelMethodBasedExpressionEvaluator();

  private final RedissonBlockLockableAspect aspect =
      new RedissonBlockLockableAspect(expressionEvaluator, lockService);

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

  @RedissonBlockLockable(key = "'user:' + #id")
  public String autoRenewalMethod(Long id) {
    return "auto:" + id;
  }

  @RedissonBlockLockable(prefix = "biz:", key = "#id", expireTime = 5, timeUnit = TimeUnit.SECONDS)
  public String fixedLeaseMethod(Long id) {
    return "fixed:" + id;
  }

  @RedissonBlockLockable(key = "#id", expireTime = 0)
  public String zeroExpireTimeMethod(Long id) {
    return "zero:" + id;
  }

  private static ProceedingJoinPoint joinPointOf(String methodName, Object... args)
      throws NoSuchMethodException {
    Method method = RedissonBlockLockableAspectTest.class.getMethod(methodName, Long.class);
    MethodSignature signature = mock(MethodSignature.class);
    when(signature.getMethod()).thenReturn(method);

    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(signature);
    when(joinPoint.getArgs()).thenReturn(args);
    return joinPoint;
  }

  private static RedissonBlockLockable annotationOf(String methodName)
      throws NoSuchMethodException {
    return RedissonBlockLockableAspectTest.class
        .getMethod(methodName, Long.class)
        .getAnnotation(RedissonBlockLockable.class);
  }

  @Test
  @DisplayName("未配置过期时间时使用看门狗续期锁，密钥由注解前缀 + SpEL 结果拼接")
  void shouldUseAutoRenewalWhenExpireTimeNotConfigured() throws Throwable {
    String key = ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "user:7";

    ProceedingJoinPoint joinPoint = joinPointOf("autoRenewalMethod", 7L);
    when(autoRenewal.doInLock(eq(key), anyCallable())).thenReturn("result");

    Object result = aspect.around(joinPoint, annotationOf("autoRenewalMethod"));

    assertThat(result).isEqualTo("result");
    verify(autoRenewal).doInLock(eq(key), anyCallable());
    verify(lockService, never()).fixedLease();
  }

  @Test
  @DisplayName("配置过期时间时使用固定租期锁，并按 timeUnit 换算")
  void shouldUseFixedLeaseWhenExpireTimeConfigured() throws Throwable {
    ProceedingJoinPoint joinPoint = joinPointOf("fixedLeaseMethod", 7L);
    when(fixedLease.doInLock(
            eq(ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "biz:7"),
            eq(Duration.ofSeconds(5)),
            anyCallable()))
        .thenReturn("result");

    Object result = aspect.around(joinPoint, annotationOf("fixedLeaseMethod"));

    assertThat(result).isEqualTo("result");
    verify(fixedLease)
        .doInLock(
            eq(ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "biz:7"),
            eq(Duration.ofSeconds(5)),
            anyCallable());
    verify(lockService, never()).autoRenewal();
  }

  @Test
  @DisplayName("expireTime 为 0 时仍走看门狗续期锁（判断条件为 expireTime > 0）")
  void shouldUseAutoRenewalWhenExpireTimeIsZero() throws Throwable {
    String key = ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "7";

    ProceedingJoinPoint joinPoint = joinPointOf("zeroExpireTimeMethod", 7L);
    when(autoRenewal.doInLock(eq(key), anyCallable())).thenReturn("result");

    Object result = aspect.around(joinPoint, annotationOf("zeroExpireTimeMethod"));

    assertThat(result).isEqualTo("result");
    verify(autoRenewal).doInLock(eq(key), anyCallable());
    verify(lockService, never()).fixedLease();
  }

  @Test
  @DisplayName("把被拦截的方法与参数交给 SpEL 求值器")
  void shouldPassMethodAndArgumentsToExpressionEvaluator() throws Throwable {
    SpelMethodBasedExpressionEvaluator evaluator = mock(SpelMethodBasedExpressionEvaluator.class);
    RedissonBlockLockableAspect mockedAspect =
        new RedissonBlockLockableAspect(evaluator, lockService);
    Method method =
        RedissonBlockLockableAspectTest.class.getMethod("autoRenewalMethod", Long.class);
    ProceedingJoinPoint joinPoint = joinPointOf("autoRenewalMethod", 7L);
    Object[] args = joinPoint.getArgs();
    RedissonBlockLockable annotation = annotationOf("autoRenewalMethod");
    when(evaluator.getValue(method, args, annotation.key(), String.class)).thenReturn("value");
    when(autoRenewal.doInLock(eq(ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "value"), anyCallable()))
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
            eq(ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "biz:3"),
            any(Duration.class),
            anyCallable()))
        .thenAnswer(invocation -> ((ThrowingCallable<?>) invocation.getArgument(2)).call());

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
            eq(ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + "user:3"), anyCallable()))
        .thenAnswer(invocation -> ((ThrowingCallable<?>) invocation.getArgument(1)).call());

    assertThatThrownBy(() -> aspect.around(joinPoint, annotationOf("autoRenewalMethod")))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("boom");
  }
}
