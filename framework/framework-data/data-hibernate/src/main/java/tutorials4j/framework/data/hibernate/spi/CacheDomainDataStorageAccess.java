package tutorials4j.framework.data.hibernate.spi;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.cache.Cache;
import org.springframework.util.SerializationUtils;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;
import tutorials4j.framework.cache.core.support.CacheType;

/**
 * Hibernate 二级缓存领域数据的存储访问实现。
 *
 * <p>将 Hibernate 的缓存读写操作委托给 Spring {@link Cache}，缓存区域名与缓存类型在构造时指定， 并延迟初始化对应的 Spring Cache 实例。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class CacheDomainDataStorageAccess implements DomainDataStorageAccess {
  /** 缓存区域名称。 */
  private final String regionName;

  /** 缓存类型，用于选择对应的缓存管理器。 */
  private final CacheType cacheType;

  /** 延迟初始化的 Spring Cache 实例。 */
  private Cache cache;

  /**
   * 获取 Spring Cache 实例，未初始化时先执行初始化。
   *
   * @return Spring Cache 实例
   */
  private Cache getCache() {
    if (cache == null) {
      initCache();
    }
    return cache;
  }

  /** 根据缓存类型查找缓存管理器并获取指定区域的缓存实例。 */
  private synchronized void initCache() {
    if (cache != null) {
      return;
    }

    cache =
        CacheManagerCreatorFactory.instance
            .findFirstCacheManagerCreator(cacheType.getCreatorCategories())
            .getInstance()
            .getCache(regionName);
  }

  /**
   * 从缓存中读取数据。
   *
   * @param key 缓存键
   * @param session Hibernate 会话
   * @return 缓存值，不存在时返回 {@code null}
   */
  @Override
  public Object getFromCache(Object key, SharedSessionContractImplementor session) {
    Cache.ValueWrapper valueWrapper = getCache().get(wrapper(key));
    return valueWrapper != null ? valueWrapper.get() : null;
  }

  /**
   * 将数据写入缓存。
   *
   * @param key 缓存键
   * @param value 缓存值
   * @param session Hibernate 会话
   */
  @Override
  public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
    getCache().put(wrapper(key), value);
  }

  /**
   * 判断缓存中是否包含指定键。
   *
   * @param key 缓存键
   * @return 包含时返回 {@code true}
   */
  @Override
  public boolean contains(Object key) {
    return getCache().get(wrapper(key)) != null;
  }

  /** 清空该区域的全部缓存数据。 */
  @Override
  public void evictData() {
    getCache().clear();
  }

  /**
   * 移除指定键的缓存数据。
   *
   * @param key 缓存键
   */
  @Override
  public void evictData(Object key) {
    getCache().evict(key);
  }

  /** 释放缓存资源，无实际清理逻辑。 */
  @Override
  public void release() {}

  /**
   * 将 Hibernate 缓存键转换为字符串缓存键，查询键使用 Murmur3 哈希处理。
   *
   * @param key Hibernate 缓存键
   * @return 字符串形式的缓存键
   */
  private String wrapper(Object key) {
    if (key instanceof QueryKey queryKey) {
      byte[] serialized = SerializationUtils.serialize(queryKey);
      return Hashing.murmur3_128().hashBytes(serialized).toString();
    }
    return String.valueOf(key);
  }
}
