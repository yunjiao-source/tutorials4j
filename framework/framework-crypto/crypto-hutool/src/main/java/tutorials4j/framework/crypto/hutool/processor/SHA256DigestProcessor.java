package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;

/**
 * 基于 Hutool 的 SHA256 摘要处理器，支持盐值、盐位置与摘要次数配置。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SHA256DigestProcessor implements DigestProcessor {
  /** SHA256 摘要器实例。 */
  private final Digester sha256;

  /**
   * 创建一个使用默认参数（无盐、摘要一次）的 SHA256 摘要处理器。
   *
   * @return SHA256 摘要处理器实例
   */
  public static SHA256DigestProcessor create() {
    return create(null, 0, 1);
  }

  /**
   * 使用指定盐值与摘要参数创建 SHA256 摘要处理器。
   *
   * @param salt 盐值，可为 null
   * @param saltPosition 盐值位置
   * @param digestCount 摘要次数
   * @return SHA256 摘要处理器实例
   */
  public static SHA256DigestProcessor create(String salt, int saltPosition, int digestCount) {
    Digester sha256 = new Digester(DigestAlgorithm.SHA256);
    if (StringUtils.isNotBlank(salt)) {
      sha256.setSalt(salt.getBytes(StandardCharsets.UTF_8));
    }
    sha256.setSaltPosition(saltPosition);
    sha256.setDigestCount(digestCount);
    return new SHA256DigestProcessor(sha256);
  }

  /** 返回摘要算法类别，固定为 SHA256。 */
  @Override
  public DigestCategory getCategory() {
    return DigestCategory.SHA256;
  }

  /** SHA256 摘要不需要密钥，返回 null。 */
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

  /** SHA256 摘要不支持密钥，返回 null。 */
  @Override
  public DigestProcessor newInstance(SecretKey secretKey) {
    // 不支持的方法
    return null;
  }

  /** 使用 UTF-8 字符集计算内容的 SHA256 摘要。 */
  @Override
  public String digest(String content) {
    return digest(content, StandardCharsets.UTF_8);
  }

  /** 使用指定字符集计算内容的 SHA256 摘要。 */
  @Override
  public String digest(String content, Charset charset) {
    return sha256.digestHex(content, charset);
  }
}
