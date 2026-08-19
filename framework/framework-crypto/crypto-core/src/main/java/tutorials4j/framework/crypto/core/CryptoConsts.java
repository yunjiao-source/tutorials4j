package tutorials4j.framework.crypto.core;

import tutorials4j.framework.common.core.SymbolConsts;

/**
 * 加密模块常量定义接口，提供 PKCS#8 公钥格式相关的常量与工具方法。
 *
 * @author Yun Jiao
 */
public interface CryptoConsts {
  /** PKCS#8 公钥的起始标记。 */
  String PKCS8_PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";

  /** PKCS#8 公钥的结束标记。 */
  String PKCS8_PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

  /**
   * 为给定的公钥内容补充 PKCS#8 格式的起始/结束标记并按行拼接。
   *
   * @param key 原始公钥内容（Base64 字符串）
   * @return 带 PKCS#8 标记的完整公钥字符串
   */
  static String appendPkcs8Padding(String key) {
    return PKCS8_PUBLIC_KEY_BEGIN
        + SymbolConsts.NEW_LINE
        + key
        + SymbolConsts.NEW_LINE
        + PKCS8_PUBLIC_KEY_END;
  }
}
