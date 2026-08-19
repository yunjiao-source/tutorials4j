package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 示例功能模块应用的主启动类。
 *
 * <p>通过 {@link SpringApplication} 启动 Spring Boot 应用，聚合特性（feature）相关的示例模块。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleFeatureApplication {
  /** 应用入口方法。 */
  public static void main(String[] args) {
    SpringApplication.run(ExampleFeatureApplication.class, args);
  }
}
