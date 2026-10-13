package tutorials4j.toolkit.redisson.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;
import tutorials4j.toolkit.redisson.lock.RedissonBlockLockService;
import tutorials4j.toolkit.redisson.lock.RedissonBlockLockableAspect;
import tutorials4j.toolkit.redisson.lock.RedissonLockServiceTestFixture;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockService;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockableAspect;

/**
 * {@link RedissonToolkitAutoConfiguration} 自动装配测试。
 *
 * @author YunJiao Yang
 */
class RedissonToolkitAutoConfigurationTest {
  private static final String LOCK_KEY = "job:1";

  private final RedissonClient redissonClient = mock(RedissonClient.class);
  private final RLock lock = mock(RLock.class);

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(RedissonToolkitAutoConfiguration.class))
          .withBean(RedissonClient.class, () -> redissonClient)
          .withBean(
              SpelMethodBasedExpressionEvaluator.class, SpelMethodBasedExpressionEvaluator::new);

  @BeforeEach
  void setUp() {
    RedissonLockServiceTestFixture.useRedissonClient(null);
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
  @DisplayName("装配两个锁服务（静态单例）与两个切面")
  void shouldRegisterBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(RedissonToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(RedissonBlockLockService.class);
          assertThat(context).hasSingleBean(RedissonReentrantLockService.class);
          assertThat(context).hasSingleBean(RedissonBlockLockableAspect.class);
          assertThat(context).hasSingleBean(RedissonReentrantLockableAspect.class);
          assertThat(context.getBean(RedissonBlockLockService.class))
              .isSameAs(RedissonBlockLockService.instance);
          assertThat(context.getBean(RedissonReentrantLockService.class))
              .isSameAs(RedissonReentrantLockService.instance);
        });
  }

  @Test
  @DisplayName("容器中的 RedissonClient 被注入到阻塞式锁服务")
  void shouldWireRedissonClientIntoBlockLockService() {
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    contextRunner.run(
        context ->
            RedissonBlockLockService.instance
                .fixedLease()
                .doInLock(LOCK_KEY, Duration.ofSeconds(1), task));

    assertThat(executed).isTrue();
    verify(redissonClient).getLock(LOCK_KEY);
    verify(lock).lock(1_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("容器中的 RedissonClient 被注入到可重入锁服务")
  void shouldWireRedissonClientIntoReentrantLockService() throws InterruptedException {
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    AtomicBoolean executed = new AtomicBoolean(false);
    Runnable task = () -> executed.set(true);

    contextRunner.run(
        context ->
            RedissonReentrantLockService.instance
                .fixedLease()
                .doInLock(LOCK_KEY, Duration.ofSeconds(1), Duration.ofSeconds(2), task));

    assertThat(executed).isTrue();
    verify(lock).tryLock(1_000L, 2_000L, TimeUnit.MILLISECONDS);
    verify(lock).unlock();
  }

  @Test
  @DisplayName("类标注 @AutoConfiguration")
  void shouldBeAnnotatedAsAutoConfiguration() {
    assertThat(RedissonToolkitAutoConfiguration.class.getAnnotation(AutoConfiguration.class))
        .isNotNull();
  }

  @Test
  @DisplayName("已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() {
    ClassLoader classLoader = getClass().getClassLoader();
    String expectedName = RedissonToolkitAutoConfiguration.class.getName();

    assertThat(ImportCandidates.load(AutoConfiguration.class, classLoader))
        .anyMatch(className -> className.equals(expectedName));
  }
}
