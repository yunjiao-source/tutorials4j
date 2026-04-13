package tutorials4j.framework.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tutorials4j.framework.autoconfigure.servlet.EnableCachedBody;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@EnableCachedBody
@SpringBootApplication
public class FrameworkCachedBodyApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrameworkCachedBodyApplication.class, args);
    }
}
