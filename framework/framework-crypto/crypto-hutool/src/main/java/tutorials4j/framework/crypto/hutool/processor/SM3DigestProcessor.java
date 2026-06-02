package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.digest.SM3;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SM3DigestProcessor implements DigestProcessor {
  private final SM3 sm3;

  public static SM3DigestProcessor create() {
    return create(null, 0, 1);
  }

  public static SM3DigestProcessor create(String salt, int saltPosition, int digestCount) {
    SM3 sm3 = new SM3();
    if (StringUtils.isNotBlank(salt)) {
      sm3.setSalt(salt.getBytes(StandardCharsets.UTF_8));
    }
    sm3.setSaltPosition(saltPosition);
    sm3.setDigestCount(digestCount);
    return new SM3DigestProcessor(sm3);
  }

  @Override
  public DigestCategory getCategory() {
    return DigestCategory.SM3;
  }

  @Override
  public SecretKey getSecretKey() {
    // 不支持的方法
    return null;
  }

  @Override
  public DigestProcessor newInstance() {
    return create();
  }

  @Override
  public DigestProcessor newInstance(SecretKey secretKey) {
    // 不支持的方法
    return null;
  }

  @Override
  public String digest(String content) {
    return digest(content, StandardCharsets.UTF_8);
  }

  @Override
  public String digest(String content, Charset charset) {
    return sm3.digestHex(content, charset);
  }
}
