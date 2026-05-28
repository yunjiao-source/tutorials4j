package tutorials4j.springboot3.web.apicrypto.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加解密自动配置类 自动初始化加解密相关Bean，无需手动配置
 *
 * @author Yun Jiao
 */
@Configuration // 配置类
@ConditionalOnWebApplication // 仅在Web环境下生效
@EnableConfigurationProperties(CryptoProperties.class) // 启用配置属性绑定
public class CryptoAutoConfiguration {

  /**
   * 初始化加解密处理器Bean 根据配置的算法类型创建对应实现类
   *
   * @param properties 加解密配置
   * @return 加解密处理器
   */
  @Bean
  @ConditionalOnMissingBean // 不存在时才创建，支持自定义覆盖
  public CryptoProcessor cryptoProcessor(CryptoProperties properties) {
    if ("aes".equalsIgnoreCase(properties.getAlgorithm())) {
      // 校验AES密钥是否配置
      if (properties.getAesKey() == null || properties.getAesKey().isEmpty()) {
        throw new IllegalArgumentException("AES密钥未配置，请设置crypto.aes-key");
      }
      return new AESCryptoProcessor(properties.getAesKey());
    }
    // 暂不支持其他算法，可扩展
    throw new UnsupportedOperationException("不支持的加密算法: " + properties.getAlgorithm());
  }

  /**
   * 初始化请求体解密Advice Bean
   *
   * @param processor 加解密处理器
   * @param properties 加解密配置
   * @return 请求体解密Advice
   */
  @Bean
  public CryptoRequestBodyAdvice cryptoRequestBodyAdvice(
      CryptoProcessor processor, CryptoProperties properties) {
    return new CryptoRequestBodyAdvice(processor, properties);
  }

  /**
   * 初始化响应体加密Advice Bean
   *
   * @param processor 加解密处理器
   * @param properties 加解密配置
   * @return 响应体加密Advice
   */
  @Bean
  public CryptoResponseBodyAdvice cryptoResponseBodyAdvice(
      CryptoProcessor processor, CryptoProperties properties) {
    return new CryptoResponseBodyAdvice(processor, properties);
  }
}
