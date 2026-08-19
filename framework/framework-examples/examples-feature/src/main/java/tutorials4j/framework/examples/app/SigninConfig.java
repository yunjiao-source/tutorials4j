package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import tutorials4j.framework.feature.signin.annotation.EnableSignInFeature;

/**
 * 登录（Signin）特性示例的配置类。
 *
 * <p>启用登录特性支持与异步处理，并在 {@code signin} Profile 下扫描 {@code tutorials4j.framework.examples.signin}
 * 包中的示例组件。
 *
 * @author Yun Jiao
 */
@EnableSignInFeature
@EnableAsync
@Configuration
@Profile("signin")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.signin"})
public class SigninConfig {}
