package tutorials4j.framework.cache.core.lock;

/**
 * 分布式锁的行为类型枚举。
 *
 * @author Yun Jiao
 */
public enum LockType {
  /** 阻塞式锁：获取不到锁时线程一直阻塞等待，直到获得锁。 */
  BLOCK,

  /** 可重入锁：支持同一线程重复获取，且可以指定等待超时时间（非阻塞等待）。 */
  REENTRANT;
}
