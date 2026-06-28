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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class HmacSHA256DigestProcessor implements DigestProcessor {
  private final HMac mac;
  protected final SecretKey secretKey;

  public static HmacSHA256DigestProcessor create() {
    return create(null);
  }

  public static HmacSHA256DigestProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateHmacSHA256Key();
      log.info(
          "{} automatically generates a random key",
          HmacSHA256DigestProcessor.class.getSimpleName());
    }

    HMac mac = new HMac(HmacAlgorithm.HmacSHA256, secretKey.symmetricKeyByte());
    return new HmacSHA256DigestProcessor(mac, secretKey);
  }

  @Override
  public DigestCategory getCategory() {
    return DigestCategory.HmacSHA256;
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
