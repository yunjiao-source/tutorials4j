package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 模板示例配置，仅在 {@code template} Profile 下生效。
 *
 * <p>扫描模板示例包 {@code tutorials4j.framework.examples.template} 中的组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("template")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.template"})
public class TemplateConfig {}
