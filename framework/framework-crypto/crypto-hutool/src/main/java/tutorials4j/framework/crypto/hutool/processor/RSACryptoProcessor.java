package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import java.nio.charset.StandardCharsets;
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
public class RSACryptoProcessor implements CryptoProcessor {
  private final RSA rsa;
  private final SecretKey secretKey;

  public static RSACryptoProcessor create() {
    return create(null);
  }

  public static RSACryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateRSAKey();
      log.info("{} automatically generates a random key", RSACryptoProcessor.class.getSimpleName());
    }

    RSA rsa = SecureUtil.rsa(secretKey.privateKeyByte(), secretKey.publicKeyByte());
    return new RSACryptoProcessor(rsa, secretKey);
  }

  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.RSA;
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
    return rsa.decryptStr(content, KeyType.PrivateKey);
  }

  @Override
  public String encrypt(String content) {
    return rsa.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
  }
}
