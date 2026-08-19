package tutorials4j.springcloud.gateway.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Cloud Gateway 网关服务的主应用启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class SpringCloudGatewayApplication {

  /**
   * 应用启动入口。
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(SpringCloudGatewayApplication.class, args);
  }
}
