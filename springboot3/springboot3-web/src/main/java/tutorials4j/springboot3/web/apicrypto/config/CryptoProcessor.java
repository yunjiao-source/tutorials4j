package tutorials4j.springboot3.web.apicrypto.config;

/**
 * 加解密处理器接口 定义加解密的标准方法，所有加密算法实现类需遵循此规范
 *
 * @author Yun Jiao
 */
public interface CryptoProcessor {

  /**
   * 加密方法
   *
   * @param content 待加密的明文内容
   * @return 加密后的密文
   * @throws CryptoException 加密异常
   */
  String encrypt(String content) throws CryptoException;

  /**
   * 解密方法
   *
   * @param content 待解密的密文内容
   * @return 解密后的明文
   * @throws CryptoException 解密异常
   */
  String decrypt(String content) throws CryptoException;
}
