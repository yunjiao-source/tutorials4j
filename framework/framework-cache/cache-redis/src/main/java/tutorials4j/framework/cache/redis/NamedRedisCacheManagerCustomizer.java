package tutorials4j.framework.cache.redis;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 命名缓存管理器初始化
 *
 * @author Yun Jiao
 */
public class NamedRedisCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {
    @Override
    public void customize(RedisCacheManager cacheManager) {
        // 调用初始化方法
        cacheManager.initializeCaches();
    }
}
