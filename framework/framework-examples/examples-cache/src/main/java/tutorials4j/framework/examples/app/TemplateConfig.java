package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * tempalte配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("template")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.template"})
public class TemplateConfig {}
