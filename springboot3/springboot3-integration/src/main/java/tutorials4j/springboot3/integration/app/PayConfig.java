package tutorials4j.springboot3.integration.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("pay")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.integration.pay"})
public class PayConfig {}
