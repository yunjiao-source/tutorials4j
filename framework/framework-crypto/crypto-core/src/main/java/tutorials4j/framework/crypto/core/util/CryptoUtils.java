package tutorials4j.framework.crypto.core.util;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.MethodParameter;
import tutorials4j.framework.crypto.core.annotation.Crypto;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CryptoUtils {
  public static boolean supported(MethodParameter parameter) {
    Crypto crypto = parameter.getMethodAnnotation(Crypto.class);
    boolean supported = ObjectUtils.isNotEmpty(crypto) && crypto.request();

    return supported;
  }
}
