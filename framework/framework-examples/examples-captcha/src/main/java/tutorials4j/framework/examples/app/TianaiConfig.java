package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 天爱验证码
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("tianai")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.tianai"})
public class TianaiConfig {}
