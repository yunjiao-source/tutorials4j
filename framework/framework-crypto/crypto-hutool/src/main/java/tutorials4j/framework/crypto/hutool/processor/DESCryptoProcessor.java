package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class DESCryptoProcessor implements CryptoProcessor {
  protected final DES des;
  protected final SecretKey secretKey;

  public static DESCryptoProcessor create() {
    return create(null);
  }

  public static DESCryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateDESKey();
      log.info("{} automatically generates a random key", DESCryptoProcessor.class.getSimpleName());
    }

    DES des = SecureUtil.des(secretKey.symmetricKeyByte());
    return new DESCryptoProcessor(des, secretKey);
  }

  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.DES;
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
    return des.decryptStr(data);
  }

  @Override
  public String encrypt(String data) {
    return des.encryptHex(data);
  }
}
