package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * API 示例应用的配置类。
 *
 * <p>仅在 {@code api} Profile 下生效，扫描 {@code tutorials4j.framework.examples.api} 包下的组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("api")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.api"})
public class ApiConfig {}
