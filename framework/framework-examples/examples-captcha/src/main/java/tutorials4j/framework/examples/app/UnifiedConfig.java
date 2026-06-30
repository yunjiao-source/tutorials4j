package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("unified")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.unified"})
public class UnifiedConfig {}
