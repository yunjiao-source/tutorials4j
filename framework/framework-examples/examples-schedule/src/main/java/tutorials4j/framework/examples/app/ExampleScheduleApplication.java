package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 定时任务示例模块的 Spring Boot 启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleScheduleApplication {

  /**
   * 应用入口方法，启动 Spring Boot 应用。
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ExampleScheduleApplication.class, args);
  }
}
