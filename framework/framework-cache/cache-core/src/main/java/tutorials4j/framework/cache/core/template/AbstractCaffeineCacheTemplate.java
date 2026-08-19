package tutorials4j.framework.cache.core.template;

import org.springframework.cache.Cache;
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

  /**
   * 构造 Caffeine 缓存模板，指定缓存名称。
   *
   * @param cacheName 缓存名称
   */
  protected AbstractCaffeineCacheTemplate(String cacheName) {
    super(cacheName);
  }

  /** 通过缓存管理器工厂获取指定名称的 Caffeine 缓存实例。 */
  @Override
  protected Cache doGetCache(String cacheName) {
    return CacheManagerCreatorFactory.instance.findCaffeineCache(cacheName);
  }
}
