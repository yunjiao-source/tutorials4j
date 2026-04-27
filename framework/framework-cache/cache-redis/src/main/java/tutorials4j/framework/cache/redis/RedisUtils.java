package tutorials4j.framework.cache.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import tutorials4j.framework.cache.core.properties.RedisOptions;
import tutorials4j.framework.common.core.SymbolConsts;
import tutorials4j.framework.common.core.TenantContextHolder;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {


    /**
     * 创建默认的缓存键前缀策略。
     *
     * <p>通过 {@link TenantContextHolder#get()} 获取全局默认前缀，
     * 然后拼接缓存名称和分隔符（双冒号）。例如，默认前缀为 "myapp"，
     * 缓存名为 "users"，则生成的完整前缀为 "myapp:users::"。
     *
     * @return 默认的 {@link CacheKeyPrefix} 实例
     */
    static CacheKeyPrefix defaultCacheKeyPrefix() {
        return name -> TenantContextHolder.get()  + SymbolConsts.COLON + name + CacheKeyPrefix.SEPARATOR;
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
            return name -> TenantContextHolder.get() + SymbolConsts.COLON + prefix + SymbolConsts.COLON + name + CacheKeyPrefix.SEPARATOR;
        } else {
            return name -> TenantContextHolder.get() + SymbolConsts.COLON + prefix + name + CacheKeyPrefix.SEPARATOR;
        }
    }

    /**
     * 根据给定的 {@link RedisOptions} 属性填充 {@link RedisCacheConfiguration} 配置。
     *
     * @param configuration 原始的 {@link RedisCacheConfiguration} 实例，将基于它进行修改
     * @param prop          包含具体缓存配置的 {@link RedisOptions} 对象
     * @return 填充后的 {@link RedisCacheConfiguration} 实例
     */
    static RedisCacheConfiguration fillConfiguration(RedisCacheConfiguration configuration,
                                                      RedisOptions prop) {

        if (prop.getTimeToLive() != null) {
            configuration = configuration.entryTtl(prop.getTimeToLive());
        }

        if (!prop.isCacheNullValues()) {
            configuration = configuration.disableCachingNullValues();
        }

        if (prop.isUseKeyPrefix() && StringUtils.isNotBlank(prop.getKeyPrefix())) {
            configuration = configuration.computePrefixWith(RedisUtils.defaultCacheKeyPrefix(prop.getKeyPrefix()));
        }

        return configuration;
    }
}
