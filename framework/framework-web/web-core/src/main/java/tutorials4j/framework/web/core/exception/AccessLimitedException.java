package tutorials4j.framework.web.core.exception;

/**
 * 访问限制异常
 *
 * @author Yun Jiao
 */
public class AccessLimitedException extends WebFrameworkException{
    public AccessLimitedException() {
        super();
    }

    public AccessLimitedException(Throwable cause) {
        super(cause);

    }
}
