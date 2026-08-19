package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;

/**
 * 灵活的 Caffeine 缓存管理器，扩展自 {@link CaffeineCacheManager}。
 *
 * <p>支持为每个缓存名称单独配置参数，通过 {@link NamedCacheProperties#getCaches()} 获取特定缓存的配置。
 * 如果某个缓存名称存在单独配置，则基于该配置创建对应的原生 Caffeine 缓存；否则回退使用 {@link NamedCacheProperties#getDefaults()}
 * 中的全局默认配置。
 *
 * @author Yun Jiao
 * @see CaffeineCacheManager
 * @see NamedCacheProperties
 */
@Slf4j
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager {
  private final NamedCacheProperties properties;

  /**
   * 使用给定的全局配置构造一个缓存管理器。
   *
   * <p>会根据全局默认配置中的 {@code cacheNullValues} 开关设置是否允许缓存 {@code null} 值。
   *
   * @param properties 全局 Caffeine 缓存配置属性
   */
  public FlexibleCaffeineCacheManager(NamedCacheProperties properties) {
    this.properties = properties;
    this.setAllowNullValues(
        Objects.equals(properties.getDefaults().getCacheNullValues(), Boolean.TRUE));
  }

  /**
   * 使用给定的全局配置和一组缓存名称构造缓存管理器。
   *
   * <p>除初始化指定的缓存名称外，同样会根据全局默认配置中的 {@code cacheNullValues} 开关 设置是否允许缓存 {@code null} 值。
   *
   * @param properties 全局 Caffeine 缓存配置属性
   * @param cacheNames 初始化的缓存名称列表
   */
  public FlexibleCaffeineCacheManager(NamedCacheProperties properties, String... cacheNames) {
    super(cacheNames);
    this.properties = properties;
    this.setAllowNullValues(
        Objects.equals(properties.getDefaults().getCacheNullValues(), Boolean.TRUE));
  }

  /**
   * 创建指定缓存名称的原生 Caffeine 缓存对象。
   *
   * <p>若 {@link NamedCacheProperties#getCaches()} 中包含该缓存名称的单独配置， 则基于该配置构建缓存；
   * 否则记录一条告警日志并回退使用全局默认配置。无论哪种方式，都会先通过 {@code options.applyDefaults(...)} 将默认配置中未设置的属性合并进来， 再经
   * {@link CaffeineUtils#copyOption} 复制到 {@link Caffeine} 构建器并创建缓存。
   *
   * @param name 缓存名称
   * @return 原生 Caffeine 缓存实例
   */
  @Override
  protected Cache<Object, Object> createNativeCaffeineCache(String name) {
    Map<String, NamedCacheOptions> optionsMap = properties.getCaches();

    // 获取独立配置
    NamedCacheOptions options = optionsMap.get(name);
    if (options == null) {
      // 使用默认配置
      log.warn("未配置缓存，将使用默认配置： name={}", name);
      options = properties.getDefaults();
    }

    // 合并默认配置
    options.applyDefaults(properties.getDefaults());

    Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
    CaffeineUtils.copyOption(caffeine, options);

    if (log.isDebugEnabled()) {
      log.debug("Caffeine 缓存初始化, name={}, options={}", name, options);
    }
    return caffeine.build();
  }
}
