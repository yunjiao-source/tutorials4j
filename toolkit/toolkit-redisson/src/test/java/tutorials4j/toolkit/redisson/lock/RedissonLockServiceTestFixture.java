package tutorials4j.toolkit.redisson.lock;

import java.lang.reflect.Field;
import org.redisson.api.RedissonClient;

/**
 * 测试辅助：Redisson 锁服务是进程内单例，其内部的 {@code FixedLease}/{@code AutoRenewal} 在首次访问时捕获 当时的 {@link
 * RedissonClient}，测试需要通过反射清空缓存并替换客户端，保证用例之间互不影响。
 *
 * @author Yun Jiao
 */
public final class RedissonLockServiceTestFixture {

  private RedissonLockServiceTestFixture() {}

  /** 同时为两个锁服务替换 {@link RedissonClient}，并清空已缓存的内置锁对象。 */
  public static void useRedissonClient(RedissonClient redissonClient) {
    resetLocks();
    RedissonBlockLockService.instance.setRedissonClient(redissonClient);
    RedissonReentrantLockService.instance.setRedissonClient(redissonClient);
  }

  /** 清空两个锁服务中缓存的 FixedLease / AutoRenewal。 */
  public static void resetLocks() {
    setField(RedissonBlockLockService.class, RedissonBlockLockService.instance, "fixedLease", null);
    setField(
        RedissonBlockLockService.class, RedissonBlockLockService.instance, "autoRenewal", null);
    setField(
        RedissonReentrantLockService.class,
        RedissonReentrantLockService.instance,
        "fixedLease",
        null);
    setField(
        RedissonReentrantLockService.class,
        RedissonReentrantLockService.instance,
        "autoRenewal",
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
