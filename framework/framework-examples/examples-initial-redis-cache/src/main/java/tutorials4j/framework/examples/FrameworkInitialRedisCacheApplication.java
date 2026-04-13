package tutorials4j.framework.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tutorials4j.framework.autoconfigure.redis.EnableInitialRedisCache;

/**
 * 主应用类
 *
 * @author yangyunjiao
 */
@EnableInitialRedisCache
@SpringBootApplication
public class FrameworkInitialRedisCacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrameworkInitialRedisCacheApplication.class, args);
    }
}
