package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("signin")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.signin"})
public class SigninConfig {}
