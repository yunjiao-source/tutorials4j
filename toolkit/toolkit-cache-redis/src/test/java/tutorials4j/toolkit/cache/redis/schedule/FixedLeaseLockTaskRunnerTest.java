package tutorials4j.toolkit.cache.redis.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.cache.redis.lock.RedisLockServiceTestFixture;
import tutorials4j.toolkit.cache.redis.script.RedisScriptExecutor;
import tutorials4j.toolkit.core.constant.DefaultConsts;

/**
 * {@link FixedLeaseLockTaskRunner} 单元测试：固定租期锁下的任务执行与异常处理。
 *
 * @author YunJiao Yang
 */
class FixedLeaseLockTaskRunnerTest {
  private static final String LOCK_KEY = DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + "job:1";
  private static final long EXPIRE_MILLIS = 60_000L;

  private final RedisScriptExecutor scriptExecutor = mock(RedisScriptExecutor.class);

  @BeforeEach
  void setUp() {
    RedisLockServiceTestFixture.useScriptExecutor(scriptExecutor);
  }

  @AfterEach
  void tearDown() {
    RedisLockServiceTestFixture.resetLocks();
  }

  static class TestRunner implements FixedLeaseLockTaskRunner {
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
  @DisplayName("获取到锁时执行任务并释放锁")
  void shouldRunTaskUnderLock() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(EXPIRE_MILLIS))).thenReturn(true);
    TestRunner runner = new TestRunner();

    runner.run(Map.of("id", "1"));

    assertThat(runner.executions).containsExactly(Map.of("id", "1"));
    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("未获取到锁时静默跳过任务")
  void shouldSkipTaskWhenLockNotAcquired() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(EXPIRE_MILLIS)))
        .thenReturn(false);
    TestRunner runner = new TestRunner();

    assertThatCode(() -> runner.run(Map.of("id", "1"))).doesNotThrowAnyException();

    assertThat(runner.executions).isEmpty();
    verify(scriptExecutor, never()).deleteIfSame(anyString(), anyString());
  }

  @Test
  @DisplayName("任务异常被捕获记录，且锁仍被释放")
  void shouldSwallowTaskFailureAndReleaseLock() {
    when(scriptExecutor.setIfAbsent(eq(LOCK_KEY), anyString(), eq(EXPIRE_MILLIS))).thenReturn(true);
    TestRunner runner = new TestRunner();
    runner.failing = true;

    assertThatCode(() -> runner.run(Map.of("id", "1"))).doesNotThrowAnyException();

    assertThat(runner.executions).isEmpty();
    verify(scriptExecutor).deleteIfSame(eq(LOCK_KEY), anyString());
  }

  @Test
  @DisplayName("默认前缀，等待时间为 null")
  void shouldUseDefaultLockableAttributes() {
    TestRunner runner = new TestRunner();

    assertThat(runner.prefix()).isEqualTo("");
    assertThat(runner.waitTime()).isNull();
    assertThat(runner.key(Map.of("id", "9"))).isEqualTo("job:9");
  }
}
