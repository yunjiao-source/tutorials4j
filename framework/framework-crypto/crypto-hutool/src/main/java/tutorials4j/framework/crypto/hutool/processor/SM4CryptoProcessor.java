package tutorials4j.framework.crypto.hutool.processor;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.hutool.SecretKeyGenerator;

/**
 * 基于 Hutool 的 SM4 国密对称加密处理器，负责 SM4 算法的加密与解密。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SM4CryptoProcessor implements CryptoProcessor {
  /** SM4 加密器实例。 */
  private final SM4 sm4;

  /** SM4 对称密钥。 */
  protected final SecretKey secretKey;

  /**
   * 创建一个使用自动生成随机密钥的 SM4 处理器。
   *
   * @return SM4 处理器实例
   */
  public static SM4CryptoProcessor create() {
    return create(null);
  }

  /**
   * 使用指定密钥创建 SM4 处理器，密钥为空时自动生成随机密钥。
   *
   * @param secretKey SM4 对称密钥，可为 null
   * @return SM4 处理器实例
   */
  public static SM4CryptoProcessor create(SecretKey secretKey) {
    if (secretKey == null) {
      secretKey = SecretKeyGenerator.generateSM4Key();
      log.info("{} automatically generates a random key", SM4CryptoProcessor.class.getSimpleName());
    }

    SM4 sm4 = SmUtil.sm4(secretKey.symmetricKeyByte());
    return new SM4CryptoProcessor(sm4, secretKey);
  }

  /** 返回加密算法类别，固定为 SM4。 */
  @Override
  public CryptoCategory getCategory() {
    return CryptoCategory.SM4;
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

  /** 使用 SM4 解密给定的密文。 */
  @Override
  public String decrypt(String data) {
    return sm4.decryptStr(data);
  }

  /** 使用 SM4 加密给定的明文，返回十六进制密文。 */
  @Override
  public String encrypt(String data) {
    return sm4.encryptHex(data);
  }
}
