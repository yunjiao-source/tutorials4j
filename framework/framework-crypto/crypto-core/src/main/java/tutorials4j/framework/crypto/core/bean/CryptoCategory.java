package tutorials4j.framework.crypto.core.bean;

/**
 * 加密算法类别枚举，定义框架支持的所有对称与非对称加密算法。
 *
 * @author Yun Jiao
 */
public enum CryptoCategory {
  /** AES 对称加密算法。 */
  AES,
  /** DES 对称加密算法。 */
  DES,
  /** SM2 国密非对称加密算法。 */
  SM2,
  /** SM4 国密对称加密算法。 */
  SM4,
  /** RSA 非对称加密算法。 */
  RSA;
}
