package tutorials4j.framework.cache.core.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 命名缓存配置属性。
 *
 * <p>用于配置多个独立命名的缓存实例，例如 Caffeine 或 Redis 缓存。 通过配置文件中的 {@code tutorials4j.cache.named} 前缀进行绑定。
 * 包含全局默认配置和每个缓存名称对应的个性化配置。
 *
 * @author Yun Jiao
 * @see NamedCacheOptions
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE_NAMED)
public class NamedCacheProperties {
  /** 命名缓存的名称前缀，默认 {@code named:}，用于区分普通缓存与命名缓存。 */
  private String cacheNamePrefix = "named:";

  /** 全局默认缓存配置，当某个具体缓存未定义配置时使用该默认值。 */
  @NestedConfigurationProperty private NamedCacheOptions defaults = new NamedCacheOptions();

  /** 命名缓存配置映射，键为缓存名称，值为该缓存特有的配置选项。 可为每个缓存独立设置 TTL、空值缓存开关等参数。 */
  private Map<String, NamedCacheOptions> caches = new HashMap<>();
}
