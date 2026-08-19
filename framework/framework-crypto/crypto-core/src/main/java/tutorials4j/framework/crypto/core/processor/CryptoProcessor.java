package tutorials4j.framework.crypto.core.processor;

import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;

/**
 * 加密处理器接口，定义统一的文本加解密能力。
 *
 * <p>不同的实现对应不同的加密类别（如对称加密、非对称加密等），提供获取加密类别、密钥 以及创建新处理器实例的能力。
 *
 * @author Yun Jiao
 * @see CryptoCategory
 * @see SecretKey
 */
public interface CryptoProcessor {
  /** 返回该处理器支持的加密类别。 */
  CryptoCategory getCategory();

  /** 返回该处理器使用的密钥。 */
  SecretKey getSecretKey();

  /** 创建一个新的处理器实例。 */
  CryptoProcessor newInstance();

  /**
   * 使用指定密钥创建一个新的处理器实例。
   *
   * @param secretKey 密钥
   * @return 新的处理器实例
   */
  CryptoProcessor newInstance(SecretKey secretKey);

  /**
   * 解密指定内容。
   *
   * @param content 待解密的内容
   * @return 解密后的明文
   */
  String decrypt(String content);

  /**
   * 加密指定内容。
   *
   * @param content 待加密的内容
   * @return 加密后的密文
   */
  String encrypt(String content);
}
