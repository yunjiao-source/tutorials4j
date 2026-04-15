package tutorials4j.framework.common.lang;

import org.apache.commons.lang3.exception.ContextedException;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * 框架异常
 *
 * @author Yun Jiao
 */
public class FrameworkException extends ContextedException {
    public FrameworkException() {
    }

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(String message, Throwable cause, ExceptionContext context) {
        super(message, cause, context);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }
}
