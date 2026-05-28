package tutorials4j.springboot3.web.apicrypto.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加解密配置属性类 读取配置文件中crypto前缀的配置项
 *
 * @author Yun Jiao
 */
@Data // Lombok注解，自动生成get/set方法
@ConfigurationProperties(prefix = "crypto") // 绑定配置文件中crypto前缀的配置
public class CryptoProperties {
  /** 加解密功能总开关，默认关闭 */
  private boolean enabled = false;

  /** 加密算法类型，默认AES */
  private String algorithm = "aes";

  /** AES算法密钥（BASE64格式） */
  private String aesKey;
}
