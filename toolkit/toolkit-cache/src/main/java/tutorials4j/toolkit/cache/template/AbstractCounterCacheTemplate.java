package tutorials4j.toolkit.cache.template;

import org.springframework.util.Assert;
import tutorials4j.toolkit.cache.CacheType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public abstract class AbstractCounterCacheTemplate<K> extends AbstractCacheTemplate<K, Integer> {

  public AbstractCounterCacheTemplate(String cacheName, CacheType cacheType) {
    super(cacheName, cacheType);
  }

  @Override
  public Class<Integer> getValueClass() {
    return Integer.class;
  }

  @Override
  public Integer valueGenerator(K key) {
    return 1;
  }

  protected boolean check(K key, long maxTimes) {
    Integer index = get(key);
    if (index == null) {
      return false;
    }

    return index >= maxTimes;
  }

  public long counting(K key) {
    Assert.notNull(key, "key cannot be null");

    Integer index = get(key);
    if (index == null) {
      index = 0;
    }

    if (index == 0) {
      // 第一次读取剩余次数，因为缓存中还没有值，所以先创建缓存，同时缓存中计数为1。
      create(key);
    } else {
      index += 1;
      put(key, index);
    }

    return index;
  }
}
