package tutorials4j.framework.cache.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.lang.NonNull;
import tutorials4j.framework.cache.core.util.CacheUtils;
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
     * 解析缓存键前缀，去除多余的分隔符。
     *
     * <p>该方法会移除前缀字符串中的连续分隔符（如 ":::"、"::"），
     * 使得最终前缀格式整洁，避免 Redis 键中出现多余冒号。
     *
     * @param cacheKeyPrefix 缓存键前缀对象，通常由 Spring Data Redis 提供
     * @return 清理后的前缀字符串，不含多余的分隔符
     */
    static String parseCacheNamePrefix(CacheKeyPrefix cacheKeyPrefix) {
        String keyPrefix = cacheKeyPrefix.compute("");
        keyPrefix = keyPrefix.replaceAll(":::", "").replaceAll("::", "");
        return keyPrefix;
    }

    /**
     * 创建默认的缓存键前缀策略。
     *
     * <p>通过 {@link CacheUtils#defaultCacheNamePrefix()} 获取全局默认前缀，
     * 然后拼接缓存名称和分隔符（双冒号）。例如，默认前缀为 "myapp"，
     * 缓存名为 "users"，则生成的完整前缀为 "myapp:users::"。
     *
     * @return 默认的 {@link CacheKeyPrefix} 实例
     */
    static CacheKeyPrefix defaultCacheKeyPrefix() {
        return name -> CacheUtils.defaultCacheNamePrefix().get() + name + CacheKeyPrefix.SEPARATOR;
    }

    /**
     * 创建带自定义前缀的缓存键前缀策略。
     *
     * <p>若传入的 {@code prefix} 为空或空白，则回退到 {@link #defaultCacheKeyPrefix()}。
     * 否则，如果前缀末尾没有冒号，会自动补充一个冒号，然后组合全局默认前缀、自定义前缀、缓存名称和分隔符。
     *
     * @param prefix 自定义前缀，可为空
     * @return 组合后的 {@link CacheKeyPrefix} 实例
     */
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
     * 生成基于类名、方法名和参数列表的缓存键生成器。
     *
     * <p>规则：{@code 目标类名:方法名:[参数1, 参数2, ...]}
     * <ul>
     *     <li>类名：目标对象的 {@link Class#getSimpleName()}</li>
     *     <li>方法名：当前执行的方法名称</li>
     *     <li>参数列表：通过 {@link Arrays#toString(Object[])} 格式化</li>
     * </ul>
     *
     * <p>此生成器可确保在同一类、同一方法、相同参数的情况下生成相同的缓存键，
     * 适用于需要细粒度缓存控制的场景。
     *
     * @return 自定义的 {@link KeyGenerator} 实例
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
