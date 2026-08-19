package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;

/**
 * Caffeine 缓存工具接口。
 *
 * <p>提供静态工具方法，用于将 {@link NamedCacheOptions} 中的配置项复制到 {@link Caffeine} 构建器上，从而便捷地创建符合配置的原生 Caffeine
 * 缓存。
 *
 * @author Yun Jiao
 * @see Caffeine
 * @see NamedCacheOptions
 */
public interface CaffeineUtils {

  /**
   * 将给定的 {@link NamedCacheOptions} 配置项复制到 {@link Caffeine} 构建器中。
   *
   * <p>复制的内容包括：初始容量、最大容量、访问后过期时间（{@code expireAfterAccess}）以及写入后过期时间 （{@code expireAfterWrite}，取自
   * {@code timeToLive}）；若开启了统计开关则同时启用统计记录 （{@code recordStats}）。其中访问后过期时间与写入后过期时间仅在配置值非 {@code
   * null} 时才设置。
   *
   * @param caffeine 目标 Caffeine 构建器（不能为 null）
   * @param options 源配置选项（不能为 null）
   */
  static void copyOption(Caffeine<Object, Object> caffeine, NamedCacheOptions options) {
    caffeine
        .initialCapacity(options.getCaffeine().getInitialCapacity())
        .maximumSize(options.getCaffeine().getMaximumSize());
    if (options.getCaffeine().getExpireAfterAccess() != null) {
      caffeine.expireAfterAccess(options.getCaffeine().getExpireAfterAccess());
    }

    if (options.getTimeToLive() != null) {
      caffeine.expireAfterWrite(options.getTimeToLive());
    }

    if (Objects.equals(options.getEnableStatistics(), Boolean.TRUE)) {
      caffeine.recordStats();
    }
  }
}
