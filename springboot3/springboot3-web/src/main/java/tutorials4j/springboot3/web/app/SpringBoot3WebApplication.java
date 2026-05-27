package tutorials4j.springboot3.web.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tutorials4j.springboot3.common.JpaCommonConfiguration;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@Import(JpaCommonConfiguration.class)
@SpringBootApplication
public class SpringBoot3WebApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBoot3WebApplication.class, args);
  }
}
