package tutorials4j.springboot3.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Profile("demo2")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.demo2"})
public class Demo2Config {}
