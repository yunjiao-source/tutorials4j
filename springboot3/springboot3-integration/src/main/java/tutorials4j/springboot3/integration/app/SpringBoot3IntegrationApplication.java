package tutorials4j.springboot3.integration.app;

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
public class SpringBoot3IntegrationApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBoot3IntegrationApplication.class, args);
  }
}
