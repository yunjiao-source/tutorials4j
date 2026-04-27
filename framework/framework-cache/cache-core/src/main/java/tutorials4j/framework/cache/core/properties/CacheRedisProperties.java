package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存属性
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_REDIS)
public class CacheRedisProperties extends RedisOptions{
    /**
     * 命名缓存配置
     */
    private Map<String, RedisOptions> namedCaches = new HashMap<>();
}
