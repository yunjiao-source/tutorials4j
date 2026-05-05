package tutorials4j.framework.cache.core.exception;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 缓存异常
 *
 * @author Yun Jiao
 */
public class CacheFrameworkException extends FrameworkRuntimeException {
    public CacheFrameworkException() {
    }

    public CacheFrameworkException(String message) {
        super(message);
    }

    public CacheFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheFrameworkException(String message, Throwable cause, ExceptionContext context) {
        super(message, cause, context);
    }

    public CacheFrameworkException(Throwable cause) {
        super(cause);
    }
}
