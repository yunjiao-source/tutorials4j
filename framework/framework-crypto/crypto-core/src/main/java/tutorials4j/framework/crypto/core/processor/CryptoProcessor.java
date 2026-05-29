package tutorials4j.framework.crypto.core.processor;

import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.CryptoCategory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CryptoProcessor {
  CryptoCategory getCategory();

  SecretKey getSecretKey();

  String decrypt(String content);

  String encrypt(String content);
}
