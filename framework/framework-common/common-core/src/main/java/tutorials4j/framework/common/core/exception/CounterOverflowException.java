package tutorials4j.framework.common.core.exception;

/**
 * 计数器溢出
 *
 * @author Yun Jiao
 */
public class CounterOverflowException extends FrameworkException {
    public CounterOverflowException(String message) {
        super("计数器溢出:" + message);
    }
}
