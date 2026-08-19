package tutorials4j.framework.cache.core.template;

import org.springframework.cache.Cache;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;

/**
 * 多级缓存模板抽象类。
 *
 * <p>通过 {@link CacheManagerCreatorFactory#findMultiLevelCache(String)} 获取底层多级缓存实例（通常为一级本地缓存 +
 * 二级远程缓存）。 子类需实现 {@link #getValueClass()} 和 {@link #valueGenerator(Object)} 等方法。
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public abstract class AbstractMultiLevelCacheTemplate<K, V> extends AbstractCacheTemplate<K, V> {

  /**
   * 构造多级缓存模板，指定缓存名称。
   *
   * @param cacheName 缓存名称
   */
  protected AbstractMultiLevelCacheTemplate(String cacheName) {
    super(cacheName);
  }

  /** 通过缓存管理器工厂获取指定名称的多级缓存实例。 */
  @Override
  protected Cache doGetCache(String cacheName) {
    return CacheManagerCreatorFactory.instance.findMultiLevelCache(cacheName);
  }
}
