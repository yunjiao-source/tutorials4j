package tutorials4j.framework.cache.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.lang.NonNull;
import tutorials4j.framework.cache.core.CacheUtils;
import tutorials4j.framework.common.core.SymbolConsts;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {
    /**
     * TODO
     * @param cacheKeyPrefix
     * @return
     */
    static String parseCacheNamePrefix(CacheKeyPrefix cacheKeyPrefix) {
        String keyPrefix = cacheKeyPrefix.compute("");
        keyPrefix = keyPrefix.replaceAll(":::", "").replaceAll("::", "");
        return keyPrefix;
    }

    static CacheKeyPrefix defaultCacheKeyPrefix() {
        return name -> CacheUtils.defaultCacheNamePrefix().get() + name + CacheKeyPrefix.SEPARATOR;
    }

    static CacheKeyPrefix defaultCacheKeyPrefix(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return defaultCacheKeyPrefix();
        }

        if (!prefix.endsWith(SymbolConsts.COLON)) {
            return name -> CacheUtils.defaultCacheNamePrefix().get() + prefix + SymbolConsts.COLON + name + CacheKeyPrefix.SEPARATOR;
        } else {
            return name -> CacheUtils.defaultCacheNamePrefix().get() + prefix + name + CacheKeyPrefix.SEPARATOR;
        }
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
