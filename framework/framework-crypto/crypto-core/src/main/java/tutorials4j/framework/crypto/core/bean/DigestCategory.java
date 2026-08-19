package tutorials4j.framework.crypto.core.bean;

/**
 * 摘要算法类别枚举，定义框架支持的所有消息摘要算法。
 *
 * @author Yun Jiao
 */
public enum DigestCategory {
  /** SM3 国密摘要算法。 */
  SM3,
  /** SHA256 摘要算法。 */
  SHA256,
  /** HmacSHA256 带密钥的消息摘要算法。 */
  HmacSHA256,
  /** HmacSHA512 带密钥的消息摘要算法。 */
  HmacSHA512;
}
