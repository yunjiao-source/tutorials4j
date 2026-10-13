package tutorials4j.toolkit.cache.redis.lock;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import tutorials4j.toolkit.cache.redis.script.RedisScriptExecutor;
import tutorials4j.toolkit.core.autoconfigure.ScheduledExecutionOptions;
import tutorials4j.toolkit.core.concurrent.ScheduledThreadPoolExecutorHolder;

/**
 * 测试辅助：{@link RedisLockService} 是进程内单例，其内部的 {@code FixedLease}/{@code AutoRenewal} 在首次访问时 捕获当时的
 * {@link RedisScriptExecutor}，测试需要通过反射清空缓存并替换执行器，保证用例之间互不影响。
 *
 * @author YunJiao Yang
 */
public final class RedisLockServiceTestFixture {

  private RedisLockServiceTestFixture() {}

  /** 替换单例中的脚本执行器，并清空已缓存的 FixedLease / AutoRenewal。 */
  public static void useScriptExecutor(RedisScriptExecutor scriptExecutor) {
    resetLocks();
    RedisLockService.instance.setRedisScriptExecutor(scriptExecutor);
  }

  /** 清空单例中缓存的 FixedLease / AutoRenewal。 */
  public static void resetLocks() {
    setField(RedisLockService.class, RedisLockService.instance, "fixedLease", null);
    setField(RedisLockService.class, RedisLockService.instance, "autoRenewal", null);
  }

  /** 初始化 toolkit-core 的计划任务线程池（自动续期锁依赖它）。 */
  public static ScheduledThreadPoolExecutor initScheduler() {
    ScheduledThreadPoolExecutorHolder.instance.initExecutor(new ScheduledExecutionOptions());
    return ScheduledThreadPoolExecutorHolder.instance.getExecutor();
  }

  /** 清空 toolkit-core 的计划任务线程池，用于验证依赖缺失时的行为。 */
  public static void resetScheduler() {
    setField(
        ScheduledThreadPoolExecutorHolder.class,
        ScheduledThreadPoolExecutorHolder.instance,
        "executor",
        null);
    setField(
        ScheduledThreadPoolExecutorHolder.class,
        ScheduledThreadPoolExecutorHolder.instance,
        "options",
        null);
  }

  private static void setField(Class<?> type, Object target, String name, Object value) {
    try {
      Field field = type.getDeclaredField(name);
      field.setAccessible(true);
      field.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("重置 " + type.getSimpleName() + "." + name + " 失败", e);
    }
  }
}
