package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * 基于 Hutool 的 SM2 国密非对称加密处理器，使用公钥加密、私钥解密。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SM2CryptoProcessor implements CryptoProcessor {
  /** SM2 加密器实例。 */
  private final SM2 sm2;

  /** SM2 密钥对。 */
  private final SecretKey secretKey;

  /**
   * 创建一个使用自动生成随机密钥对的 SM2 处理器。
   *
   * @return SM2 处理器实例
   */
  public static SM2CryptoProcessor create() {
    return create(null);
  }

  /**
   * 使用指定密钥对创建 SM2 处理器，密钥为空时自动生成随机密钥对。
   *
   * @param secretKey SM2 密钥对，可为 null
   * @return SM2 处理器实例
   */
  public static SM2CryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateSM2Key();
      log.info("{} automatically generates a random key", SM2CryptoProcessor.class.getSimpleName());
    }

    SM2 sm2 = new SM2(secretKey.privateKeyByte(), secretKey.publicKeyByte());
    return new SM2CryptoProcessor(sm2, secretKey);
  }

  /** 返回加密算法类别，固定为 SM2。 */
  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.SM2;
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
    return sm2.decryptStr(content, KeyType.PrivateKey);
  }

  /**
   * 使用公钥加密给定的明文，返回 Base64 编码的密文。
   *
   * @throws IllegalArgumentException 当明文为空时抛出
   */
  @Override
  public String encrypt(String content) {
    if (content == null || content.isEmpty()) {
      throw new IllegalArgumentException("SM2 cannot encrypt empty data");
    }
    return sm2.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
  }
}
