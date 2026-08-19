package tutorials4j.springcloud.resource.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Cloud 资源服务器示例应用的主启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class SpringCloudResourceApplication {
  /**
   * 应用启动入口。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(SpringCloudResourceApplication.class, args);
  }
}
