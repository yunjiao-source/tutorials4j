package tutorials4j.framework.web.core;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.FrameworkRuntimeException;

/**
 * web客户端异常
 *
 * @author Yun Jiao
 */
public class WebClientFrameworkException extends FrameworkRuntimeException {
    public WebClientFrameworkException() {
    }

    public WebClientFrameworkException(String message) {
        super(message);
    }

    public WebClientFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebClientFrameworkException(String message, Throwable cause, ExceptionContext context) {
        super(message, cause, context);
    }

    public WebClientFrameworkException(Throwable cause) {
        super(cause);
    }
}
