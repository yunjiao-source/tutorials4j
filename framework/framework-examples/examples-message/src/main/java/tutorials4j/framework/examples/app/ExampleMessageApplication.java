package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Yun Jiao
 */
@SpringBootApplication
public class ExampleMessageApplication {
  /** 启动数据访问示例应用。 */
  public static void main(String[] args) {
    SpringApplication.run(ExampleMessageApplication.class, args);
  }
}
