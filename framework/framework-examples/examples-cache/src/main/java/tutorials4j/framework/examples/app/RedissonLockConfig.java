package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * redisson lock
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("redisson-lock")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.lock.redisson"})
public class RedissonLockConfig {

}
