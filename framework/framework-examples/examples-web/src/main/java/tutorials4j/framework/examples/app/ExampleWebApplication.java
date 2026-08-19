package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * examples-web 示例模块的 Spring Boot 启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleWebApplication {
  /**
   * Spring Boot 应用入口方法。
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ExampleWebApplication.class, args);
  }
}
