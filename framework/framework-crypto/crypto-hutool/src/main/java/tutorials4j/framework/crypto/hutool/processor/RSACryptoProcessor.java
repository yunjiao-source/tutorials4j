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
 * 基于 Hutool 的 RSA 非对称加密处理器，使用公钥加密、私钥解密。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class RSACryptoProcessor implements CryptoProcessor {
  /** RSA 加密器实例。 */
  private final RSA rsa;

  /** RSA 密钥对。 */
  private final SecretKey secretKey;

  /**
   * 创建一个使用自动生成随机密钥对的 RSA 处理器。
   *
   * @return RSA 处理器实例
   */
  public static RSACryptoProcessor create() {
    return create(null);
  }

  /**
   * 使用指定密钥对创建 RSA 处理器，密钥为空时自动生成随机密钥对。
   *
   * @param secretKey RSA 密钥对，可为 null
   * @return RSA 处理器实例
   */
  public static RSACryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateRSAKey();
      log.info("{} automatically generates a random key", RSACryptoProcessor.class.getSimpleName());
    }

    RSA rsa = SecureUtil.rsa(secretKey.privateKeyByte(), secretKey.publicKeyByte());
    return new RSACryptoProcessor(rsa, secretKey);
  }

  /** 返回加密算法类别，固定为 RSA。 */
  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.RSA;
  }

  /** 返回当前使用的密钥对。 */
  @Override
  public SecretKey getSecretKey() {
    return secretKey;
  }

  /** 创建一个使用随机密钥的新处理器实例。 */
  @Override
  public CryptoProcessor newInstance() {
    return create();
  }

  /** 使用指定密钥创建一个新处理器实例。 */
  @Override
  public CryptoProcessor newInstance(SecretKey secretKey) {
    return create(secretKey);
  }

  /** 使用私钥解密给定的密文。 */
  @Override
  public String decrypt(String content) {
    return rsa.decryptStr(content, KeyType.PrivateKey);
  }

  /** 使用公钥加密给定的明文，返回 Base64 编码的密文。 */
  @Override
  public String encrypt(String content) {
    return rsa.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
  }
}
