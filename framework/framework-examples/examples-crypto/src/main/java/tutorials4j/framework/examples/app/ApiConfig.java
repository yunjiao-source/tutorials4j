package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 简单配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("api")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.api"})
public class ApiConfig {}
