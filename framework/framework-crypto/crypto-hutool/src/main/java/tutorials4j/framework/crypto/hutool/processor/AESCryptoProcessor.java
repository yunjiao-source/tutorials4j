package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
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
public class AESCryptoProcessor implements CryptoProcessor {
  protected final AES aes;
  protected final SecretKey secretKey;

  public static AESCryptoProcessor create() {
    return create(SecretKeyGenerator.generateASEKey());
  }

  public static AESCryptoProcessor create(SecretKey secretKey) {
    Assert.notNull(secretKey, "'secretKey' must not be null");

    AES aes = SecureUtil.aes(secretKey.symmetricKeyByte());
    return new AESCryptoProcessor(aes, secretKey);
  }

  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.AES;
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
    return aes.decryptStr(data);
  }

  @Override
  public String encrypt(String data) {
    return aes.encryptHex(data);
  }
}
