package tutorials4j.springboot3.integration.restsignature;

/**
 * 签名异常
 *
 * @author Yun Jiao
 */
public class SignatureException extends RuntimeException {
  public SignatureException(String message) {
    super(message);
  }
}
