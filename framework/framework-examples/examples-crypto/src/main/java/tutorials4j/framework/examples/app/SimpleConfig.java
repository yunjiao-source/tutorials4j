package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 组合缓存应用配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("simple")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.simple"})
public class SimpleConfig {}
