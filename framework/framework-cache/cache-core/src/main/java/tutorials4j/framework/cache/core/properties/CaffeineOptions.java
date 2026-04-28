package tutorials4j.framework.cache.core.properties;

import lombok.Data;

import java.time.Duration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class CaffeineOptions {
    private Integer initialCapacity = 100;
    private Long maximumSize = 1000L;
    private Duration expireAfterWrite;
    private Duration expireAfterAccess;
    private Boolean recordStats;

    // 高级配置
    private Boolean weakKeys = false;
    private Boolean weakValues = false;
    private Boolean softValues = false;

    /**
     * 将未设置的属性（null）从默认配置中合并。
     *
     * @param defaults 默认配置
     */
    public void mergeNullValue(CaffeineOptions defaults) {
        if (defaults == null) return;

        if (this.maximumSize == null) {
            this.maximumSize = defaults.getMaximumSize();
        }

        if (this.initialCapacity == null) {
            this.initialCapacity = defaults.getInitialCapacity();
        }

        if (this.expireAfterAccess == null) {
            this.expireAfterAccess =defaults.getExpireAfterAccess();
        }

        if (this.expireAfterWrite == null) {
            this.expireAfterWrite = defaults.getExpireAfterWrite();
        }

        if (this.recordStats == null) {
            this.recordStats = defaults.recordStats;
        }

        if (this.weakKeys == null) this.weakKeys = defaults.weakKeys;
        if (this.weakValues == null) this.weakValues = defaults.weakValues;
        if (this.softValues == null) this.softValues = defaults.softValues;
    }
}
