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
    private Duration refreshAfterWrite;
    private Boolean recordStats;

    public void mergeNullValue(CaffeineOptions target) {
        if (this.maximumSize == null) {
            this.maximumSize = target.getMaximumSize();
        }

        if (this.initialCapacity == null) {
            this.initialCapacity = target.getInitialCapacity();
        }

        if (this.expireAfterAccess == null) {
            this.expireAfterAccess =target.getExpireAfterAccess();
        }

        if (this.expireAfterWrite == null) {
            this.expireAfterWrite = target.getExpireAfterWrite();
        }

        if (this.refreshAfterWrite == null) {
            this.refreshAfterWrite = target.getRefreshAfterWrite();
        }

        if (this.recordStats == null) {
            this.recordStats = target.recordStats;
        }
    }
}
