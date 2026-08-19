package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * 基于 Hutool 的 AES 对称加密处理器，负责 AES 算法的加密与解密。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class AESCryptoProcessor implements CryptoProcessor {
  /** AES 加密器实例。 */
  protected final AES aes;

  /** AES 对称密钥。 */
  protected final SecretKey secretKey;

  /**
   * 创建一个使用自动生成随机密钥的 AES 处理器。
   *
   * @return AES 处理器实例
   */
  public static AESCryptoProcessor create() {
    return create(null);
  }

  /**
   * 使用指定密钥创建 AES 处理器，密钥为空时自动生成随机密钥。
   *
   * @param secretKey AES 对称密钥，可为 null
   * @return AES 处理器实例
   */
  public static AESCryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateASEKey();
      log.info("{} automatically generates a random key", AESCryptoProcessor.class.getSimpleName());
    }

    AES aes = SecureUtil.aes(secretKey.symmetricKeyByte());
    return new AESCryptoProcessor(aes, secretKey);
  }

  /** 返回加密算法类别，固定为 AES。 */
  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.AES;
  }

  /** 返回当前使用的对称密钥。 */
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

  /** 使用 AES 解密给定的密文。 */
  @Override
  public String decrypt(String data) {
    return aes.decryptStr(data);
  }

  /** 使用 AES 加密给定的明文，返回十六进制密文。 */
  @Override
  public String encrypt(String data) {
    return aes.encryptHex(data);
  }
}
