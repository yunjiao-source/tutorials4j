package tutorials4j.framework.cache.core.lock;

/**
 * 分布式锁服务顶层接口。
 *
 * <p>定义锁的基本元信息：缓存类型和锁类型。具体锁执行逻辑由实现类提供， 通常配合 {@link LockServiceFactory} 通过类型组合获取具体实现。
 *
 * @author Yun Jiao
 */
public interface LockService {

  /**
   * 获取锁的缓存类型（如本地、Redis、Redisson）。
   *
   * @return 锁缓存类型，非 {@code null}
   */
  LockCacheType getLockCacheType();

  /**
   * 获取锁的行为类型（如阻塞、可重入）。
   *
   * @return 锁类型，非 {@code null}
   */
  LockType getLockType();
}
