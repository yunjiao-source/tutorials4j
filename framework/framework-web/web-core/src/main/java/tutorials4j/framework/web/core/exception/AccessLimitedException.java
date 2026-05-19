package tutorials4j.framework.web.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 访问限制异常
 *
 * @author Yun Jiao
 */
public class AccessLimitedException extends FrameworkRuntimeException {
  public AccessLimitedException() {
    super();
  }

  public AccessLimitedException(Throwable cause) {
    super(cause);
  }
}
