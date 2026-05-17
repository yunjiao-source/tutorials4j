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
@Profile("lock")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.lock"})
public class LockConfig {}
