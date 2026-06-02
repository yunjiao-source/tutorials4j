package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.DigestCategory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class HmacSHA512DigestProcessor implements DigestProcessor {
  private final HMac mac;
  protected final SecretKey secretKey;

  public static HmacSHA512DigestProcessor create() {
    return create(SecretKeyGenerator.generateHmacSHA512Key());
  }

  public static HmacSHA512DigestProcessor create(SecretKey secretKey) {
    Assert.notNull(secretKey, "'secretKey' must not be empty or blank");

    HMac mac = new HMac(HmacAlgorithm.HmacSHA512, secretKey.symmetricKeyByte());
    return new HmacSHA512DigestProcessor(mac, secretKey);
  }

  @Override
  public DigestCategory getCategory() {
    return DigestCategory.HmacSHA512;
  }

  @Override
  public SecretKey getSecretKey() {
    return secretKey;
  }

  @Override
  public DigestProcessor newInstance() {
    return create();
  }

  @Override
  public DigestProcessor newInstance(SecretKey secretKey) {
    return create(secretKey);
  }

  @Override
  public String digest(String content) {
    return digest(content, StandardCharsets.UTF_8);
  }

  @Override
  public String digest(String content, Charset charset) {
    return mac.digestHex(content, charset);
  }
}
