package tutorials4j.framework.cache.core.template;

import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;

/**
 * 多级缓存模板抽象类。
 * <p>
 * 通过 {@link CacheManagerCreatorFactory#getMultiLevelCache(String)} 获取底层多级缓存实例（通常为一级本地缓存 + 二级远程缓存）。
 * 子类需实现 {@link #getValueClass()} 和 {@link #valueGenerator(Object)} 等方法。
 * </p>
 *
 * @param <K> 缓存键类型
 * @param <V> 缓存值类型
 * @author Yun Jiao
 */
public abstract class AbstractMultiLevelCacheTemplate<K, V> extends AbstractCacheTemplate<K, V> {
    protected AbstractMultiLevelCacheTemplate(String cacheName) {
        super(cacheName);
    }

    @Override
    protected void initCache() {
        cache = CacheManagerCreatorFactory.INSTANCE.getMultiLevelCache(cacheName);
    }
}
