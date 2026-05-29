package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
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
public class RSACryptoProcessor implements CryptoProcessor {
  private final RSA rsa;
  private final SecretKey secretKey;

  public static RSACryptoProcessor create() {
    return create(SecretKeyGenerator.generateRSAKey());
  }

  public static RSACryptoProcessor create(SecretKey secretKey) {
    Assert.notNull(secretKey, "'secretKey' must not be null");

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
  public String decrypt(String content) {
    return rsa.decryptStr(content, KeyType.PrivateKey);
  }

  @Override
  public String encrypt(String content) {
    return rsa.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
  }
}
