package tutorials4j.framework.data.core;

import tutorials4j.framework.common.lang.FrameworkRuntimeException;

/**
 * Data异常
 *
 * @author Yun Jiao
 */
public class DataFrameworkException extends FrameworkRuntimeException {
    public DataFrameworkException(String message) {
        super(message);
    }

    public DataFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
