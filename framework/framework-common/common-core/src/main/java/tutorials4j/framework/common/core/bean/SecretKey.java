package tutorials4j.framework.common.core.bean;

import cn.hutool.core.util.HexUtil;
import java.io.Serializable;
import java.time.Instant;

/**
 * 密钥信息记录
 *
 * @param identity 数据存储身份标识
 * @param symmetricKeyHex 对称加密算法秘钥, Hex 格式
 * @param publicKeyHex 服务器端非对称加密算法公钥, Hex 格式
 * @param privateKeyHex 服务器端非对称加密算法私钥, Hex 格式
 * @param timestamp 创建时间戳
 */
public record SecretKey(
    String identity,
    String symmetricKeyHex,
    String publicKeyHex,
    String privateKeyHex,
    Instant timestamp)
    implements Serializable {

  public byte[] symmetricKeyByte() {
    return HexUtil.decodeHex(symmetricKeyHex);
  }

  public byte[] publicKeyByte() {
    return HexUtil.decodeHex(publicKeyHex);
  }

  public byte[] privateKeyByte() {
    return HexUtil.decodeHex(privateKeyHex);
  }

  public SecretKey(String symmetricKeyHex) {
    this(null, symmetricKeyHex, null, null, Instant.now());
  }

  public SecretKey(String publicKeyHex, String privateKeyHex) {
    this(null, null, publicKeyHex, privateKeyHex, Instant.now());
  }

  public SecretKey(
      String identity, String symmetricKeyHex, String publicKeyHex, String privateKeyHex) {
    this(identity, symmetricKeyHex, publicKeyHex, privateKeyHex, Instant.now());
  }
}
