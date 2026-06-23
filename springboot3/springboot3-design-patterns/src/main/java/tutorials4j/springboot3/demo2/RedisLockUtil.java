package tutorials4j.springboot3.demo2;

import java.util.concurrent.ThreadLocalRandom;

/** Redis分布式锁工具类（简化版，适配整套框架） */
public class RedisLockUtil {
  // 模拟Redis加锁
  public static boolean tryLock(String key, long timeout) {
    // 60%的几率返回true
    return ThreadLocalRandom.current().nextInt(99) < 60;
  }

  // 模拟Redis解锁
  public static boolean unLock(String key) {
    // 实际项目替换为 Lua 脚本原子解锁
    return true;
  }
}
