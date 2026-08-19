package tutorials4j.springcloud.gateway.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * simple 环境的网关配置：启用并扫描 simple 示例包下的组件。
 *
 * @author Yun Jiao
 */
@Profile("simple")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springcloud.gateway.simple"})
public class SimpleProfile {}
