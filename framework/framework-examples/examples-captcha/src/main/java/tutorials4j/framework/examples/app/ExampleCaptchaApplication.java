package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 验证码示例工程启动类。
 *
 * <p>用于演示框架验证码（Captcha）模块的集成与使用。
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleCaptchaApplication {
  /** 启动验证码示例应用。 */
  public static void main(String[] args) {
    SpringApplication.run(ExampleCaptchaApplication.class, args);
  }
}
