package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Yun Jiao
 */
@Configuration
@Profile("apikey")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.apikey"})
public class ApiKeyConfig {}
