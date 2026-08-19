package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 加解密示例应用的主启动类。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleCryptoApplication {
  /**
   * 应用入口。
   *
   * @param args 启动参数
   */
  public static void main(String[] args) {
    SpringApplication.run(ExampleCryptoApplication.class, args);
  }
}
