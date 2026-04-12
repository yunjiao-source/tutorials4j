package tutorials4j.springboot3;

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
public class SpringBoot3RestSignatureApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3RestSignatureApplication.class, args);
    }
}
