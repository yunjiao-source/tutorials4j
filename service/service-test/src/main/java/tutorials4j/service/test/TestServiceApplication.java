package tutorials4j.service.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@EnableScheduling
@SpringBootApplication
public class TestServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(TestServiceApplication.class, args);
  }
}
