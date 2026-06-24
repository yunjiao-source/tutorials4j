package tutorials4j.springboot3.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Profile("demo4")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.demo4"})
public class Demo4Config {}
