package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@EnableScheduling
@SpringBootApplication
public class ExampleScheduleApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExampleScheduleApplication.class, args);
  }
}
