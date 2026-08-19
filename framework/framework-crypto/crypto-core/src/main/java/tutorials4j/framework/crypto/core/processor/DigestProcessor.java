package tutorials4j.framework.crypto.core.processor;

import java.nio.charset.Charset;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.DigestCategory;

/**
 * 摘要处理器接口，定义统一的摘要计算能力。
 *
 * <p>不同的实现对应不同的摘要类别，提供获取类别、密钥以及创建新处理器实例的能力。
 *
 * @author Yun Jiao
 * @see DigestCategory
 * @see SecretKey
 */
public interface DigestProcessor {
  /** 返回该处理器支持的摘要类别。 */
  DigestCategory getCategory();

  /** 返回该处理器使用的密钥。 */
  SecretKey getSecretKey();

  /** 创建一个新的处理器实例。 */
  DigestProcessor newInstance();

  /**
   * 使用指定密钥创建一个新的处理器实例。
   *
   * @param secretKey 密钥
   * @return 新的处理器实例
   */
  DigestProcessor newInstance(SecretKey secretKey);

  /**
   * 计算指定内容的摘要。
   *
   * @param content 待计算摘要的内容
   * @return 摘要结果
   */
  String digest(String content);

  /**
   * 按指定字符集计算指定内容的摘要。
   *
   * @param content 待计算摘要的内容
   * @param charset 字符集
   * @return 摘要结果
   */
  String digest(String content, Charset charset);
}
