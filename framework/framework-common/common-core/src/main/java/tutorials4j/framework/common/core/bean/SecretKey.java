package tutorials4j.framework.common.core.bean;

import cn.hutool.core.util.HexUtil;
import java.io.Serializable;
import java.time.Instant;

/**
 * 密钥信息记录，封装对称加密密钥或非对称加密密钥对（Hex 格式）。
 *
 * <p>提供从 Hex 字符串解码为字节数组的便捷方法，并记录创建时间戳；实现 {@link Serializable} 便于持久化与传输。
 *
 * @param identity 数据存储身份标识
 * @param symmetricKeyHex 对称加密算法秘钥, Hex 格式
 * @param publicKeyHex 服务器端非对称加密算法公钥, Hex 格式
 * @param privateKeyHex 服务器端非对称加密算法私钥, Hex 格式
 * @param timestamp 创建时间戳
 * @author Yun Jiao
 */
public record SecretKey(
    String identity,
    String symmetricKeyHex,
    String publicKeyHex,
    String privateKeyHex,
    Instant timestamp)
    implements Serializable {

  /**
   * 将对称密钥的 Hex 字符串解码为字节数组。
   *
   * @return 对称密钥字节数组
   */
  public byte[] symmetricKeyByte() {
    return HexUtil.decodeHex(symmetricKeyHex);
  }

  /**
   * 将公钥的 Hex 字符串解码为字节数组。
   *
   * @return 公钥字节数组
   */
  public byte[] publicKeyByte() {
    return HexUtil.decodeHex(publicKeyHex);
  }

  /**
   * 将私钥的 Hex 字符串解码为字节数组。
   *
   * @return 私钥字节数组
   */
  public byte[] privateKeyByte() {
    return HexUtil.decodeHex(privateKeyHex);
  }

  /**
   * 构造仅包含对称密钥的密钥信息，创建时间戳取当前时间。
   *
   * @param symmetricKeyHex 对称密钥 Hex 字符串
   */
  public SecretKey(String symmetricKeyHex) {
    this(null, symmetricKeyHex, null, null, Instant.now());
  }

  /**
   * 构造仅包含非对称密钥对的密钥信息，创建时间戳取当前时间。
   *
   * @param publicKeyHex 公钥 Hex 字符串
   * @param privateKeyHex 私钥 Hex 字符串
   */
  public SecretKey(String publicKeyHex, String privateKeyHex) {
    this(null, null, publicKeyHex, privateKeyHex, Instant.now());
  }

  /**
   * 构造包含身份标识及密钥（对称或非对称）的密钥信息，创建时间戳取当前时间。
   *
   * @param identity 数据存储身份标识
   * @param symmetricKeyHex 对称密钥 Hex 字符串，可为空
   * @param publicKeyHex 公钥 Hex 字符串，可为空
   * @param privateKeyHex 私钥 Hex 字符串，可为空
   */
  public SecretKey(
      String identity, String symmetricKeyHex, String publicKeyHex, String privateKeyHex) {
    this(identity, symmetricKeyHex, publicKeyHex, privateKeyHex, Instant.now());
  }
}
