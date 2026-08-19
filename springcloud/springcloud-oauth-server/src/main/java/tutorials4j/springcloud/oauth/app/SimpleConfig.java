package tutorials4j.springcloud.oauth.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * simple Profile 下的配置类，扫描 {@code tutorials4j.springcloud.oauth.simple} 包中的组件。
 *
 * @author Yun Jiao
 */
@Profile("simple")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springcloud.oauth.simple"})
public class SimpleConfig {}
