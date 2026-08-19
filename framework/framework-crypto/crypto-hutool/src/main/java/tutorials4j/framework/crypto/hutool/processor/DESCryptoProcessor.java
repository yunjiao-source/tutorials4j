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
 * 基于 Hutool 的 DES 对称加密处理器，负责 DES 算法的加密与解密。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class DESCryptoProcessor implements CryptoProcessor {
  /** DES 加密器实例。 */
  protected final DES des;

  /** DES 对称密钥。 */
  protected final SecretKey secretKey;

  /**
   * 创建一个使用自动生成随机密钥的 DES 处理器。
   *
   * @return DES 处理器实例
   */
  public static DESCryptoProcessor create() {
    return create(null);
  }

  /**
   * 使用指定密钥创建 DES 处理器，密钥为空时自动生成随机密钥。
   *
   * @param secretKey DES 对称密钥，可为 null
   * @return DES 处理器实例
   */
  public static DESCryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateDESKey();
      log.info("{} automatically generates a random key", DESCryptoProcessor.class.getSimpleName());
    }

    DES des = SecureUtil.des(secretKey.symmetricKeyByte());
    return new DESCryptoProcessor(des, secretKey);
  }

  /** 返回加密算法类别，固定为 DES。 */
  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.DES;
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

  /** 使用 DES 解密给定的密文。 */
  @Override
  public String decrypt(String data) {
    return des.decryptStr(data);
  }

  /** 使用 DES 加密给定的明文，返回十六进制密文。 */
  @Override
  public String encrypt(String data) {
    return des.encryptHex(data);
  }
}
