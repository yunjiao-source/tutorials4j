package tutorials4j.springboot3.web.apicrypto.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密处理器实现类 采用CBC模式+PKCS5Padding填充方式，IV向量取自密钥前16字节
 *
 * @author Yun Jiao
 */
public class AESCryptoProcessor implements CryptoProcessor {
  // AES算法模式：AES/CBC/PKCS5Padding
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
  // AES密钥对象
  private final SecretKey secretKey;
  // 初始化向量（IV）
  private final IvParameterSpec iv;

  /**
   * 构造方法，初始化密钥和IV向量
   *
   * @param base64Key BASE64编码的AES密钥（16/24/32字节）
   */
  public AESCryptoProcessor(String base64Key) {
    // 解码BASE64格式的密钥
    byte[] keyBytes = Base64.getDecoder().decode(base64Key);
    // 创建AES密钥对象
    this.secretKey = new SecretKeySpec(keyBytes, "AES");
    // 取密钥前16字节作为IV向量（CBC模式必须指定IV）
    this.iv = new IvParameterSpec(Arrays.copyOfRange(keyBytes, 0, 16));
  }

  /**
   * AES加密实现
   *
   * @param content 待加密的明文
   * @return BASE64编码的密文
   * @throws CryptoException 加密失败异常
   */
  @Override
  public String encrypt(String content) {
    try {
      // 创建Cipher对象，指定算法模式
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      // 初始化加密模式
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
      // 执行加密并编码为BASE64字符串
      byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      throw new CryptoException("AES加密失败", e);
    }
  }

  /**
   * AES解密实现
   *
   * @param content BASE64编码的密文
   * @return 解密后的明文
   * @throws CryptoException 解密失败异常
   */
  @Override
  public String decrypt(String content) {
    try {
      // 创建Cipher对象，指定算法模式
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      // 初始化解密模式
      cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
      // 解码BASE64密文并执行解密
      byte[] decoded = Base64.getDecoder().decode(content);
      byte[] decrypted = cipher.doFinal(decoded);
      // 转换为UTF-8编码的明文
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new CryptoException("AES解密失败", e);
    }
  }
}
