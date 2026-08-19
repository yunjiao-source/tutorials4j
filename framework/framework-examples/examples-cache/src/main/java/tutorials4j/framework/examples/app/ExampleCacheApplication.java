package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 缓存示例应用启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleCacheApplication {
  /**
   * 应用入口方法。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ExampleCacheApplication.class, args);
  }
}
