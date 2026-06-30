package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import tutorials4j.framework.feature.signin.annotation.EnableSignInFeature;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@EnableSignInFeature
@EnableAsync
@Configuration
@Profile("signin")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.signin"})
public class SigninConfig {}
