package tutorials4j.framework.examples;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 应用配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("named")
public class NamedConfig {


}
