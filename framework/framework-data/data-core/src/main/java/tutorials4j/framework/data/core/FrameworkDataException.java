package tutorials4j.framework.data.core;

import tutorials4j.framework.common.lang.FrameworkRuntimeException;

/**
 * Data异常
 *
 * @author Yun Jiao
 */
public class FrameworkDataException extends FrameworkRuntimeException {
    public FrameworkDataException(String message) {
        super(message);
    }

    public FrameworkDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
