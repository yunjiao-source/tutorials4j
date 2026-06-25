package tutorials4j.springboot3.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Profile("demo5")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.demo5"})
public class Demo5Config {}
