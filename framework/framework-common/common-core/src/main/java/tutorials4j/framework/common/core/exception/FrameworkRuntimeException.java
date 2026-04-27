package tutorials4j.framework.common.core.exception;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * 框架异常
 *
 * @author Yun Jiao
 */
public class FrameworkRuntimeException extends ContextedRuntimeException {
    public FrameworkRuntimeException() {
    }

    public FrameworkRuntimeException(String message) {
        super(message);
    }

    public FrameworkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkRuntimeException(String message, Throwable cause, ExceptionContext context) {
        super(message, cause, context);
    }

    public FrameworkRuntimeException(Throwable cause) {
        super(cause);
    }
}
