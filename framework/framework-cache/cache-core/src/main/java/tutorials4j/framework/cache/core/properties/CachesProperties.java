package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.lang.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存属性
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE)
public class CachesProperties {
    private RedisCacheOptions redis = new RedisCacheOptions();

    /**
     * 命名缓存属性，定义每个缓存的过期时间等信息
     *
     * @author Yun Jiao
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RedisCacheOptions extends CacheProperties.Redis {
        /**
         * 命名缓存配置
         */
        private Map<String, CacheProperties.Redis> namedCaches = new HashMap<>();
    }
}
