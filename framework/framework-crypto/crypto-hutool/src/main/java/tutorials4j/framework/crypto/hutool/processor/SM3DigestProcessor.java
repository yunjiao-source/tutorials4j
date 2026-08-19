package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.digest.SM3;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;

/**
 * 基于 Hutool 的 SM3 国密摘要处理器，支持盐值、盐位置与摘要次数配置。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SM3DigestProcessor implements DigestProcessor {
  /** SM3 摘要器实例。 */
  private final SM3 sm3;

  /**
   * 创建一个使用默认参数（无盐、摘要一次）的 SM3 摘要处理器。
   *
   * @return SM3 摘要处理器实例
   */
  public static SM3DigestProcessor create() {
    return create(null, 0, 1);
  }

  /**
   * 使用指定盐值与摘要参数创建 SM3 摘要处理器。
   *
   * @param salt 盐值，可为 null
   * @param saltPosition 盐值位置
   * @param digestCount 摘要次数
   * @return SM3 摘要处理器实例
   */
  public static SM3DigestProcessor create(String salt, int saltPosition, int digestCount) {
    SM3 sm3 = new SM3();
    if (StringUtils.isNotBlank(salt)) {
      sm3.setSalt(salt.getBytes(StandardCharsets.UTF_8));
    }
    sm3.setSaltPosition(saltPosition);
    sm3.setDigestCount(digestCount);
    return new SM3DigestProcessor(sm3);
  }

  /** 返回摘要算法类别，固定为 SM3。 */
  @Override
  public DigestCategory getCategory() {
    return DigestCategory.SM3;
  }

  /** SM3 摘要不需要密钥，返回 null。 */
  @Override
  public SecretKey getSecretKey() {
    // 不支持的方法
    return null;
  }

  /** 创建一个使用默认参数的新处理器实例。 */
  @Override
  public DigestProcessor newInstance() {
    return create();
  }

  /** SM3 摘要不支持密钥，返回 null。 */
  @Override
  public DigestProcessor newInstance(SecretKey secretKey) {
    // 不支持的方法
    return null;
  }

  /** 使用 UTF-8 字符集计算内容的 SM3 摘要。 */
  @Override
  public String digest(String content) {
    return digest(content, StandardCharsets.UTF_8);
  }

  /** 使用指定字符集计算内容的 SM3 摘要。 */
  @Override
  public String digest(String content, Charset charset) {
    return sm3.digestHex(content, charset);
  }
}
