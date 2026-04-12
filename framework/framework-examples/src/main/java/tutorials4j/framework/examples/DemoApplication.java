package tutorials4j.framework.examples;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoApplication.class)
                .properties("spring.config.location=classpath:/myconfig.yml")
                .run(args);
    }
}
