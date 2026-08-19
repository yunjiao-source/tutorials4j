package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * 基于 Hutool 的 HmacSHA512 摘要处理器，负责 HmacSHA512 消息摘要的计算。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class HmacSHA512DigestProcessor implements DigestProcessor {
  /** HmacSHA512 摘要器实例。 */
  private final HMac mac;

  /** HmacSHA512 密钥。 */
  protected final SecretKey secretKey;

  /**
   * 创建一个使用自动生成随机密钥的 HmacSHA512 摘要处理器。
   *
   * @return HmacSHA512 摘要处理器实例
   */
  public static HmacSHA512DigestProcessor create() {
    return create(null);
  }

  /**
   * 使用指定密钥创建 HmacSHA512 摘要处理器，密钥为空时自动生成随机密钥。
   *
   * @param secretKey HmacSHA512 密钥，可为 null
   * @return HmacSHA512 摘要处理器实例
   */
  public static HmacSHA512DigestProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateHmacSHA512Key();
      log.info(
          "{} automatically generates a random key",
          HmacSHA512DigestProcessor.class.getSimpleName());
    }

    HMac mac = new HMac(HmacAlgorithm.HmacSHA512, secretKey.symmetricKeyByte());
    return new HmacSHA512DigestProcessor(mac, secretKey);
  }

  /** 返回摘要算法类别，固定为 HmacSHA512。 */
  @Override
  public DigestCategory getCategory() {
    return DigestCategory.HmacSHA512;
  }

  /** 返回当前使用的密钥。 */
  @Override
  public SecretKey getSecretKey() {
    return secretKey;
  }

  /** 创建一个使用随机密钥的新处理器实例。 */
  @Override
  public DigestProcessor newInstance() {
    return create();
  }

  /** 使用指定密钥创建一个新处理器实例。 */
  @Override
  public DigestProcessor newInstance(SecretKey secretKey) {
    return create(secretKey);
  }

  /** 使用 UTF-8 字符集计算内容的 HmacSHA512 摘要。 */
  @Override
  public String digest(String content) {
    return digest(content, StandardCharsets.UTF_8);
  }

  /** 使用指定字符集计算内容的 HmacSHA512 摘要。 */
  @Override
  public String digest(String content, Charset charset) {
    return mac.digestHex(content, charset);
  }
}
