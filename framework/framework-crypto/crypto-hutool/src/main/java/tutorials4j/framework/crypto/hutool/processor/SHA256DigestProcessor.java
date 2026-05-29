package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;
import tutorials4j.framework.crypto.core.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SHA256DigestProcessor implements DigestProcessor {
  private final Digester sha256;

  public static SHA256DigestProcessor create() {
    return create(null, 0, 1);
  }

  public static SHA256DigestProcessor create(String salt, int saltPosition, int digestCount) {
    Digester sha256 = new Digester(DigestAlgorithm.SHA256);
    if (StringUtils.isNotBlank(salt)) {
      sha256.setSalt(salt.getBytes(StandardCharsets.UTF_8));
    }
    sha256.setSaltPosition(saltPosition);
    sha256.setDigestCount(digestCount);
    return new SHA256DigestProcessor(sha256);
  }

  @Override
  public DigestCategory getCategory() {
    return DigestCategory.SHA256;
  }

  @Override
  public SecretKey getSecretKey() {
    throw new FrameworkRuntimeException("方法不支持");
  }

  @Override
  public String digest(String data) {
    return digest(data, StandardCharsets.UTF_8);
  }

  @Override
  public String digest(String data, Charset charset) {
    return sha256.digestHex(data, charset);
  }
}
