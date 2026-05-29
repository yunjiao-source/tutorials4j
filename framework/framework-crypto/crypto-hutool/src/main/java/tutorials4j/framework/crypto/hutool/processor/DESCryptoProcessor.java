package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
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
public class DESCryptoProcessor implements CryptoProcessor {
  protected final DES des;
  protected final SecretKey secretKey;

  public static DESCryptoProcessor create() {
    return create(SecretKeyGenerator.generateDESKey());
  }

  public static DESCryptoProcessor create(SecretKey secretKey) {
    Assert.notNull(secretKey, "'secretKey' must not be empty or blank");

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
  public String decrypt(String data) {
    return des.decryptStr(data);
  }

  @Override
  public String encrypt(String data) {
    return des.encryptHex(data);
  }
}
