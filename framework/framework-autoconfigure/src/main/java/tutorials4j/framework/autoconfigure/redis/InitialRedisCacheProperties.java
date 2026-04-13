package tutorials4j.framework.autoconfigure.redis;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.core.constants.BasePropertiesConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化缓存属性配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = BasePropertiesConstants.PROPERTY_PREFIX_CACHE)
public class InitialRedisCacheProperties {
    private Map<String, CacheProperties.Redis> redis = new HashMap<>();
}
