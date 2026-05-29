package tutorials4j.framework.crypto.core;

import tutorials4j.framework.common.core.SymbolConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface CryptoConsts {
  String PKCS8_PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
  String PKCS8_PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

  static String appendPkcs8Padding(String key) {
    return PKCS8_PUBLIC_KEY_BEGIN
        + SymbolConsts.NEW_LINE
        + key
        + SymbolConsts.NEW_LINE
        + PKCS8_PUBLIC_KEY_END;
  }
}
