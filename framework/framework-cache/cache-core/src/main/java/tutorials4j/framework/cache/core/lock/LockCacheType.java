package tutorials4j.framework.cache.core.lock;

/**
 * 分布式锁的缓存类型枚举。
 *
 * <p>用于区分锁底层所使用的缓存中间件或实现方式。
 *
 * @author Yun Jiao
 */
public enum LockCacheType {
  /** 基于本地内存的锁（如 JVM 锁，仅单机有效）。 */
  LOCAL,
  /** 基于 Redis 原生实现的锁（如 setnx + lua）。 */
  REDIS,
  /** 基于 Redisson 框架实现的锁（功能更丰富，支持可重入、看门狗等）。 */
  REDISSON;
}
