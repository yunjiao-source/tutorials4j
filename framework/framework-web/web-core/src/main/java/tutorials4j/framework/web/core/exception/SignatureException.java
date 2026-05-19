package tutorials4j.framework.web.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 签名异常
 *
 * @author Yun Jiao
 */
public class SignatureException extends FrameworkRuntimeException {
  public SignatureException(String message) {
    super(message);
  }

  public SignatureException(String message, Throwable cause) {
    super(message, cause);
  }
}
