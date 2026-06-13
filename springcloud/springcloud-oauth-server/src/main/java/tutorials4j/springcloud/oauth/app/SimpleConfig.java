package tutorials4j.springcloud.oauth.app;

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
@ComponentScan(basePackages = {"tutorials4j.springcloud.oauth.simple"})
public class SimpleConfig {}
