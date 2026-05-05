package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@EnableAsync
@Configuration
@Profile("security")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.security"})
public class SecurityConfig {

}
