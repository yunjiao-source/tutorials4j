package tutorials4j.springboot3.data.orm.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tutorials4j.springboot3.common.JpaCommonConfiguration;
import tutorials4j.springboot3.common.MybatisCommonConfiguration;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@Import({MybatisCommonConfiguration.class, JpaCommonConfiguration.class})
@SpringBootApplication
public class SpringBoot3DataOrmApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBoot3DataOrmApplication.class, args);
  }
}
