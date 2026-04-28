package tutorials4j.framework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import tutorials4j.framework.cache.core.properties.CaffeineOptions;

import java.util.Objects;

/**
 * Caffeine缓存工具接口
 *
 * @author Yun Jiao
 * @see Caffeine
 * @see CaffeineOptions
 */
public interface CaffeineUtils {

    /**
     * 将给定的{@link CaffeineOptions}配置项复制到{@link Caffeine}构建器中。
     * <p>复制的内容包括：初始容量、最大容量、访问后过期时间、写入后过期时间、刷新间隔以及是否启用统计信息。</p>
     *
     * @param caffeine 目标Caffeine构建器（不能为null）
     * @param options  源配置选项（不能为null）
     */
    static void copyOption(Caffeine<Object, Object> caffeine, CaffeineOptions options) {
        caffeine.initialCapacity(options.getInitialCapacity())
                .maximumSize(options.getMaximumSize());
        if (options.getExpireAfterAccess() != null) {
            caffeine.expireAfterAccess(options.getExpireAfterAccess());
        }

        if (options.getExpireAfterWrite() != null) {
            caffeine.expireAfterWrite(options.getExpireAfterWrite());
        }

        if (Objects.equals(Boolean.TRUE,options.getRecordStats())) {
            caffeine.recordStats();
        }

        // 高级特性
        if (Boolean.TRUE.equals(options.getWeakKeys())) {
            caffeine.weakKeys();
        }
        if (Boolean.TRUE.equals(options.getWeakValues())) {
            caffeine.weakValues();
        }
        if (Boolean.TRUE.equals(options.getSoftValues())) {
            caffeine.softValues();
        }
    }


}
