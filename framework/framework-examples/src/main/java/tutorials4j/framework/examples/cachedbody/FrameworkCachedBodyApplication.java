package tutorials4j.framework.examples.cachedbody;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import tutorials4j.framework.autoconfigure.servlet.EnableCachedBody;
import tutorials4j.framework.examples.DemoApplication;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@EnableCachedBody
@SpringBootApplication
public class FrameworkCachedBodyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoApplication.class)
                .properties("spring.config.location=classpath:application-common.yml,classpath:cached-body.yml")
                .run(args);
    }
}
