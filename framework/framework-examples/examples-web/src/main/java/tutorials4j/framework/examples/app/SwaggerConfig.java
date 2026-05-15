package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Swagger配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("swagger")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.swagger"})
public class SwaggerConfig {}
