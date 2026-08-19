package tutorials4j.springcloud.oauth.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Cloud OAuth2 服务端应用启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class SpringCloudOAuthApplication {
  /**
   * 应用启动入口。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(SpringCloudOAuthApplication.class, args);
  }
}
