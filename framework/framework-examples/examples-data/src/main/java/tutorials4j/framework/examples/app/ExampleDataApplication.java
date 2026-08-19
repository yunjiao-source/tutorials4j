package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 数据访问示例工程启动类。
 *
 * <p>用于演示框架数据（Data）模块的集成与使用。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleDataApplication {
  /** 启动数据访问示例应用。 */
  public static void main(String[] args) {
    SpringApplication.run(ExampleDataApplication.class, args);
  }
}
