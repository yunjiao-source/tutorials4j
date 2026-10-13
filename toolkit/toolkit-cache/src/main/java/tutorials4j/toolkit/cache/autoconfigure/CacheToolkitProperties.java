package tutorials4j.toolkit.cache.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_CACHE)
public class CacheToolkitProperties {
  private CacheType type = CacheType.redis;
}
