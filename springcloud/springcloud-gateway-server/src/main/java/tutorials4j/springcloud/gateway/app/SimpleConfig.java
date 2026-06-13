package tutorials4j.springcloud.gateway.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("simple")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springcloud.gateway.simple"})
public class SimpleConfig {}
