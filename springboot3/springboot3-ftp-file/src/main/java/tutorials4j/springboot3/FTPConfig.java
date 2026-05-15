package tutorials4j.springboot3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ftp服务配置
 *
 * @author Yun Jiao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPConfig {
  private String server;
  private int port;
  private String user;
  private String password;
}
