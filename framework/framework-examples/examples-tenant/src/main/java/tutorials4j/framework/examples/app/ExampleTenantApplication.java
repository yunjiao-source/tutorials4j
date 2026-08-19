package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 多租户示例应用的启动类。
 *
 * <p>作为 Spring Boot 应用入口，通过 {@link SpringApplication#run} 启动整个应用， 并启用自动配置以装配租户相关的示例组件。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleTenantApplication {

  /**
   * 应用入口方法，启动 Spring Boot 应用。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ExampleTenantApplication.class, args);
  }
}
