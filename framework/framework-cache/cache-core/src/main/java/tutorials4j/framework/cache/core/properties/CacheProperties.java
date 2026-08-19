package tutorials4j.framework.cache.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 核心缓存配置。
 *
 * <p>通过 {@code tutorials4j.cache} 配置前缀绑定缓存通用参数，如 Redisson 缓存与模板缓存的名称。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_CACHE)
public class CacheProperties {
  /** Redisson 缓存名称，默认 {@code redisson:cache}。 */
  private String redissonCacheName = "redisson:cache";

  /** 模板缓存名称，默认 {@code template:cache}。 */
  private String templateCacheName = "template:cache";
}
