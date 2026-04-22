package tutorials4j.framework.cache.redis;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 命名缓存管理器初始化
 *
 * 1. 虽然RedisCacheManager实现了InitializingBean接口，但RedisCacheManager创建后，并没有执行afterPropertiesSet方法
 * 2. 因为在运行期创建的缓存，使用默认配置属性创建
 *
 * @author Yun Jiao
 */
public class NamedCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {
    @Override
    public void customize(RedisCacheManager cacheManager) {
        // 调用初始化方法
        cacheManager.initializeCaches();
    }
}
