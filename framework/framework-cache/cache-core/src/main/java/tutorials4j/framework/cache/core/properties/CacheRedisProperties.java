package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存属性
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_REDIS)
public class CacheRedisProperties{
    /**
     * 命名缓存配置
     */
    private Map<String, CacheProperties.Redis> namedCaches = new HashMap<>();
}
