package tutorials4j.framework.autoconfigure.redis;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * 初始化缓存
 *
 * @author Yun Jiao
 */
@Configuration(proxyBeanMethods = false)
public class InitialRedisCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {
    @Override
    public void customize(RedisCacheManager cacheManager) {
        // 调用初始化方法
        cacheManager.initializeCaches();
    }
}
