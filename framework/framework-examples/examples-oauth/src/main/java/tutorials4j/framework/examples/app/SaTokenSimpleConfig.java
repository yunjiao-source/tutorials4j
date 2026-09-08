package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Yun Jiao
 */
@Configuration
@Profile("sa-token-simple")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.satoken.simple"})
public class SaTokenSimpleConfig {}
