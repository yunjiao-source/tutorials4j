package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 锁缓存配置。
 *
 * <p>通过 {@code tutorials4j.cache.lock} 配置前缀绑定锁相关参数，包含本地锁与 Redis 锁的配置选项。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_LOCK)
public class LockCacheProperties {
  /** 本地锁配置选项。 */
  @NestedConfigurationProperty private LocalLockOptions local = new LocalLockOptions();

  /** Redis 锁配置选项。 */
  @NestedConfigurationProperty private RedisLockOptions redis = new RedisLockOptions();
}
