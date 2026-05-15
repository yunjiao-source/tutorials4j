package tutorials4j.framework.web.core.exception;

/**
 * 签名异常
 *
 * @author Yun Jiao
 */
public class SignatureException extends WebFrameworkException {
  public SignatureException(String message) {
    super(message);
  }

  public SignatureException(String message, Throwable cause) {
    super(message, cause);
  }
}
