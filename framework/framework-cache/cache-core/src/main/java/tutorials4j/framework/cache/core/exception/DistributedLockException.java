package tutorials4j.framework.cache.core.exception;

import java.time.Duration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DistributedLockException extends CacheFrameworkException{
    public DistributedLockException() {
        super("分布式锁异常");
    }

    public DistributedLockException(String lockKey, Throwable cause) {
        super("分布式锁异常", cause);
        addContextValue("lockKey", lockKey);
    }

    public DistributedLockException(String lockKey) {
        this();
        addContextValue("lockKey", lockKey);
    }

    public DistributedLockException(String lockKey, Duration waitTime) {
        this();
        addContextValue("lockKey", lockKey);
        addContextValue("waitTime", waitTime);
    }
}
