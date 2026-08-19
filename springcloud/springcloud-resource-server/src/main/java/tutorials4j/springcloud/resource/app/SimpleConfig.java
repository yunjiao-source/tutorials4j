package tutorials4j.springcloud.resource.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 简单演示场景的配置类。
 *
 * <p>仅在 {@code simple} Profile 激活时生效，负责扫描 {@code tutorials4j.springcloud.resource.simple}
 * 包下的组件，用于演示资源服务器的简化接入方式。
 *
 * @author Yun Jiao
 */
@Profile("simple")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springcloud.resource.simple"})
public class SimpleConfig {}
