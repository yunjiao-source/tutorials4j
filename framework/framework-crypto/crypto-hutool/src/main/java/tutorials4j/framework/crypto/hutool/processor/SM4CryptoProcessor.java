package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SM4CryptoProcessor implements CryptoProcessor {
  private final SM4 sm4;
  protected final SecretKey secretKey;

  public static SM4CryptoProcessor create() {
    return create(SecretKeyGenerator.generateSM4Key());
  }

  public static SM4CryptoProcessor create(SecretKey secretKey) {
    Assert.notNull(secretKey, "'secretKey' must not be null");

    SM4 sm4 = SmUtil.sm4(secretKey.symmetricKeyByte());
    return new SM4CryptoProcessor(sm4, secretKey);
  }

  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.SM4;
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
  public String decrypt(String data) {
    return sm4.decryptStr(data);
  }

  @Override
  public String encrypt(String data) {
    return sm4.encryptHex(data);
  }
}
