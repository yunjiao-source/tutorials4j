package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 简单示例应用的配置类。
 *
 * <p>仅在 {@code simple} Profile 下生效，扫描 {@code tutorials4j.framework.examples.simple} 包下的组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("simple")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.simple"})
public class SimpleConfig {}
