package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Objects;
import tutorials4j.framework.cache.core.properties.NamedCacheOptions;

/**
 * Caffeine缓存工具接口
 *
 * @author Yun Jiao
 * @see Caffeine
 * @see NamedCacheOptions
 */
public interface CaffeineUtils {

  /**
   * 将给定的{@link NamedCacheOptions}配置项复制到{@link Caffeine}构建器中。
   *
   * <p>复制的内容包括：初始容量、最大容量、访问后过期时间、写入后过期时间、刷新间隔以及是否启用统计信息。
   *
   * @param caffeine 目标Caffeine构建器（不能为null）
   * @param options 源配置选项（不能为null）
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
