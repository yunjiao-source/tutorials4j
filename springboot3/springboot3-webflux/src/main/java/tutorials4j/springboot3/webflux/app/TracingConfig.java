package tutorials4j.springboot3.webflux.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("tracing")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.webflux.tracing"})
public class TracingConfig {}
