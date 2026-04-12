package tutorials4j.springboot3;

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
