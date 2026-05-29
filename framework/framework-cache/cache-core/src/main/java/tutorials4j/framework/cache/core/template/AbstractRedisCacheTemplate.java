package tutorials4j.framework.cache.core.template;

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
  public AbstractRedisCacheTemplate(String cacheName) {
    super(cacheName);
  }

  @Override
  protected void initCache() {
    cache = CacheManagerCreatorFactory.instance.findRedisCache(cacheName);
  }
}
