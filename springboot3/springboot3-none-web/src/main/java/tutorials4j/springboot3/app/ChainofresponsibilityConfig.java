package tutorials4j.springboot3.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Profile("chainofresponsibility")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.noneweb.chainofresponsibility"})
public class ChainofresponsibilityConfig {}
