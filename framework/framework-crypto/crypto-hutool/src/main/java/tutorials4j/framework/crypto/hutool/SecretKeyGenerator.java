package tutorials4j.framework.crypto.hutool;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import java.security.KeyPair;
import tutorials4j.framework.common.core.bean.SecretKey;

/**
 * 密钥生成器接口，提供基于 Hutool 生成各类对称密钥、HMAC 密钥与非对称密钥对的静态方法。
 *
 * <p>生成的密钥统一封装为 {@link SecretKey}，密钥字节以十六进制字符串形式表示。
 *
 * @author Yun Jiao
 */
public interface SecretKeyGenerator {
  /**
   * 生成一个 AES 对称密钥。
   *
   * @return 封装 AES 密钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateASEKey() {
    byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  /**
   * 生成一个 DES 对称密钥。
   *
   * @return 封装 DES 密钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateDESKey() {
    byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  /**
   * 生成一个 128 位的 SM4 国密对称密钥。
   *
   * @return 封装 SM4 密钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateSM4Key() {
    byte[] encoded = KeyUtil.generateKey(SM4.ALGORITHM_NAME, 128).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  /**
   * 生成一个 HmacSHA256 摘要密钥。
   *
   * @return 封装 HmacSHA256 密钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateHmacSHA256Key() {
    byte[] encoded = SecureUtil.generateKey(HmacAlgorithm.HmacSHA256.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  /**
   * 生成一个 HmacSHA512 摘要密钥。
   *
   * @return 封装 HmacSHA512 密钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateHmacSHA512Key() {
    byte[] encoded = SecureUtil.generateKey(HmacAlgorithm.HmacSHA512.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  /**
   * 生成一个 1024 位的 RSA 密钥对。
   *
   * @return 封装 RSA 公钥/私钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateRSAKey() {
    KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), 1024);
    return new SecretKey(
        HexUtil.encodeHexStr(keyPair.getPublic().getEncoded()),
        HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded()));
  }

  /**
   * 生成一个 SM2 国密密钥对。
   *
   * @return 封装 SM2 公钥/私钥的 {@link SecretKey}，值为十六进制字符串
   */
  static SecretKey generateSM2Key() {
    KeyPair keyPair = SecureUtil.generateKeyPair("sm2", 1024);
    return new SecretKey(
        HexUtil.encodeHexStr(keyPair.getPublic().getEncoded()),
        HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded()));
  }
}
