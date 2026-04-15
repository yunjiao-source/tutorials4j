package tutorials4j.framework.cache.redis;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {
    String PREFIX = "tutorials4j:cache:";

    /**
     * 规则：'tutorials4j:cache:'
     * @return {@link CacheKeyPrefix} 实例
     */
    static CacheKeyPrefix tutorials4jCacheKeyPrefix() {
        return CacheKeyPrefix.prefixed(PREFIX);
    }

    /**
     * 规则：tutorials4j:cache:${name}:
     * @param name 名称
     * @return {@link CacheKeyPrefix} 实例
     */
    static CacheKeyPrefix tutorials4jCacheKeyPrefix(String name) {
        return CacheKeyPrefix.prefixed(PREFIX + name + ":");
    }

    /**
     * 规则：类名 + 方法名 + 参数列表
     *
     * @return {@link KeyGenerator} 实例
     */
    static KeyGenerator classMethodParamsKeyGenerator() {
        return (Object target, Method method, @NonNull Object... params) -> {
            // 自定义 key 生成规则，例如：
            return target.getClass().getSimpleName() +
                    ":" +
                    method.getName() +
                    ":" +
                    Arrays.toString(params);
        };
    }
}
