package tutorials4j.framework.cache.core.template;

import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;

/**
 * Caffeine 本地缓存模板抽象类。
 *
 * <p>通过 {@link CacheManagerCreatorFactory#findCaffeineCache(String)} 获取底层 Caffeine 缓存实例。 子类需实现
 * {@link #getValueClass()} 和 {@link #valueGenerator(Object)} 等方法。
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public abstract class AbstractCaffeineCacheTemplate<K, V> extends AbstractCacheTemplate<K, V> {
  public AbstractCaffeineCacheTemplate(String cacheName) {
    super(cacheName);
  }

  @Override
  protected void initCache() {
    cache = CacheManagerCreatorFactory.instance.findCaffeineCache(cacheName);
  }
}
