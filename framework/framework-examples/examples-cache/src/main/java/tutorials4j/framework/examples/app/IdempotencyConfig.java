package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Yun Jiao
 */
@Configuration
@Profile("idempotency")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.idempotency"})
public class IdempotencyConfig {}
