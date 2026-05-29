package tutorials4j.framework.crypto.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CryptoException extends FrameworkRuntimeException {

  public CryptoException(String message) {
    super(message);
  }

  public CryptoException(String message, Throwable cause) {
    super(message, cause);
  }
}
