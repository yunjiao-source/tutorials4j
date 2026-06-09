package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_LOCK)
public class LockCacheProperties {
  @NestedConfigurationProperty private LocalLockOptions local = new LocalLockOptions();
  @NestedConfigurationProperty private RedisLockOptions reids = new RedisLockOptions();
}
