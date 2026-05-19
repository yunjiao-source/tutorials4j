package tutorials4j.framework.examples.app;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * rest接口
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("rest")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.rest"})
public class RestConfig implements CachingConfigurer {}
