package tutorials4j.framework.cache.core.template;

import org.springframework.cache.Cache;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;

/**
 * Redis 缓存模板抽象类。
 *
 * <p>通过 {@link CacheManagerCreatorFactory#findRedisCache(String)} 获取底层 Redis 缓存实例。 子类需实现 {@link
 * #getValueClass()} 和 {@link #valueGenerator(Object)} 等方法。
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public abstract class AbstractRedisCacheTemplate<K, V> extends AbstractCacheTemplate<K, V> {

  /**
   * 构造 Redis 缓存模板，指定缓存名称。
   *
   * @param cacheName 缓存名称
   */
  public AbstractRedisCacheTemplate(String cacheName) {
    super(cacheName);
  }

  /** 通过缓存管理器工厂获取指定名称的 Redis 缓存实例。 */
  @Override
  protected Cache doGetCache(String cacheName) {
    return CacheManagerCreatorFactory.instance.findRedisCache(cacheName);
  }
}
