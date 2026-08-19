package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 通用功能示例工程启动类。
 *
 * <p>用于演示框架通用（Common）模块的集成与使用。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleCommonApplication {
  /** 启动通用功能示例应用。 */
  public static void main(String[] args) {
    SpringApplication.run(ExampleCommonApplication.class, args);
  }
}
