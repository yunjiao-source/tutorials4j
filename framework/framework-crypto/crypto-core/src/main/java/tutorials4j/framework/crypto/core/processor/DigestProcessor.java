package tutorials4j.framework.crypto.core.processor;

import java.nio.charset.Charset;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.DigestCategory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface DigestProcessor {
  DigestCategory getCategory();

  SecretKey getSecretKey();

  DigestProcessor newInstance();

  DigestProcessor newInstance(SecretKey secretKey);

  String digest(String content);

  String digest(String content, Charset charset);
}
