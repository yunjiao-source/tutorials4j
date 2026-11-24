package tutorials4j.service.ai;

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
public class AiServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AiServiceApplication.class, args);
  }
}
