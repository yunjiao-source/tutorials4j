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
 * TODO
 *
 * @author Yun Jiao
 */
public interface SecretKeyGenerator {
  static SecretKey generateASEKey() {
    byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  static SecretKey generateDESKey() {
    byte[] encoded = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  static SecretKey generateSM4Key() {
    byte[] encoded = KeyUtil.generateKey(SM4.ALGORITHM_NAME, 128).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  static SecretKey generateHmacSHA256Key() {
    byte[] encoded = SecureUtil.generateKey(HmacAlgorithm.HmacSHA256.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  static SecretKey generateHmacSHA512Key() {
    byte[] encoded = SecureUtil.generateKey(HmacAlgorithm.HmacSHA512.getValue()).getEncoded();
    return new SecretKey(HexUtil.encodeHexStr(encoded));
  }

  static SecretKey generateRSAKey() {
    KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), 1024);
    return new SecretKey(
        HexUtil.encodeHexStr(keyPair.getPublic().getEncoded()),
        HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded()));
  }

  static SecretKey generateSM2Key() {
    KeyPair keyPair = SecureUtil.generateKeyPair("sm2", 1024);
    return new SecretKey(
        HexUtil.encodeHexStr(keyPair.getPublic().getEncoded()),
        HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded()));
  }
}
