package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置
 *
 * @author Yun Jiao
 */
@Configuration
@ComponentScan(basePackages = {"tutorials4j.framework.examples.jpa"})
public class AppConfig {
}
