package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.feature.satoken.annotation.EnableSaTokenFeature;

/**
 * @author Yun Jiao
 */
@EnableSaTokenFeature
@Configuration
@Profile("sa-token")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.satoken"})
public class SaTokenConfig {}
