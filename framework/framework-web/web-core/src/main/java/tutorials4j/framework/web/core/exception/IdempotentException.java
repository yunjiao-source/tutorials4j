package tutorials4j.framework.web.core.exception;

/**
 * 幂等异常
 *
 * @author Yun Jiao
 */
public class IdempotentException extends WebFrameworkException{
    public IdempotentException() {
        super();
    }

    public IdempotentException(Throwable cause) {
        super( cause);
    }
}
