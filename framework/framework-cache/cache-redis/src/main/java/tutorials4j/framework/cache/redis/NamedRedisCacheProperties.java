package tutorials4j.framework.cache.redis;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.lang.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * 命名缓存属性，定义每个缓存的过期时间等信息
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE)
public class NamedRedisCacheProperties {
    private Map<String, CacheProperties.Redis> namedRedisCaches = new HashMap<>();
}
