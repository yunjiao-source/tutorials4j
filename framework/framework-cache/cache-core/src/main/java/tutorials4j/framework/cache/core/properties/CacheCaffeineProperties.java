package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * Caffeine缓存配置属性
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_CAFFEINE)
public class CacheCaffeineProperties extends CaffeineOptions {
    private Boolean allowNullValues = true;

    /**
     * 命名缓存配置
     */
    private Map<String, CaffeineOptions> namedCaches = new HashMap<>();

}
