package tutorials4j.framework.examples.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class ExampleTenantApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleTenantApplication.class, args);
    }
}
