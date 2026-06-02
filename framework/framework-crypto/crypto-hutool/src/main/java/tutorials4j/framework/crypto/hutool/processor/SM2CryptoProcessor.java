package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SM2CryptoProcessor implements CryptoProcessor {
  private final SM2 sm2;
  private final SecretKey secretKey;

  public static SM2CryptoProcessor create() {
    return create(SecretKeyGenerator.generateSM2Key());
  }

  public static SM2CryptoProcessor create(SecretKey secretKey) {
    Assert.notNull(secretKey, "'secretKey' must not be null");

    SM2 sm2 = new SM2(secretKey.privateKeyByte(), secretKey.publicKeyByte());
    return new SM2CryptoProcessor(sm2, secretKey);
  }

  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.SM2;
  }

  @Override
  public SecretKey getSecretKey() {
    return secretKey;
  }

  @Override
  public CryptoProcessor newInstance() {
    return create();
  }

  @Override
  public CryptoProcessor newInstance(SecretKey secretKey) {
    return create(secretKey);
  }

  @Override
  public String decrypt(String content) {
    return sm2.decryptStr(content, KeyType.PrivateKey);
  }

  @Override
  public String encrypt(String content) {
    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("SM2 cannot encrypt empty data");
    }
    return sm2.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
  }
}
