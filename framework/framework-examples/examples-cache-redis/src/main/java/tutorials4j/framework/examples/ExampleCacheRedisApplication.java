package tutorials4j.framework.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@EnableCaching
@SpringBootApplication
public class ExampleCacheRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleCacheRedisApplication.class, args);
    }
}
