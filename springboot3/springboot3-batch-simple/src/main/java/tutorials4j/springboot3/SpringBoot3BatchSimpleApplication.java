package tutorials4j.springboot3;

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
public class SpringBoot3BatchSimpleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3BatchSimpleApplication.class, args);
    }
}
