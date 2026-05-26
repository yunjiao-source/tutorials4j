package tutorials4j.springboot3.integration.ftp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ftp服务配置
 *
 * @author Yun Jiao
 */
@Data
@Component
@ConfigurationProperties(prefix = "ftp")
public class FTPProperties {
  private String server;
  private int port;
  private String user;
  private String password;
}
