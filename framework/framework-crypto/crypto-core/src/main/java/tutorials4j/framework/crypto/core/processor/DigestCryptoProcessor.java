package tutorials4j.framework.crypto.core.processor;

import java.nio.charset.Charset;

/**
 * 摘要加密处理器接口，扩展自 {@link CryptoProcessor}。
 *
 * <p>提供密钥创建与摘要计算能力，适用于 MD5、SHA 等摘要算法。
 *
 * @author Yun Jiao
 */
public interface DigestCryptoProcessor extends CryptoProcessor {
  /** 创建一个新的摘要密钥。 */
  String createKey();

  /**
   * 计算指定数据的摘要。
   *
   * @param data 待计算摘要的数据
   * @return 摘要结果
   */
  String digest(String data);

  /**
   * 按指定字符集计算指定数据的摘要。
   *
   * @param data 待计算摘要的数据
   * @param charset 字符集
   * @return 摘要结果
   */
  String digest(String data, Charset charset);
}
