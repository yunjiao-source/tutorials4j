package tutorials4j.framework.examples.app;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * redis命名缓存应用配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("named")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cacheable"})
public class NamedConfig {


}
