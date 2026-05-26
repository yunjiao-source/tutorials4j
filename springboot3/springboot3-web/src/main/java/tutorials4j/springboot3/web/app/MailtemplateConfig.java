package tutorials4j.springboot3.web.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Profile("mailtemplate")
@Configuration
@ComponentScan(basePackages = {"tutorials4j.springboot3.web.mailtemplate"})
public class MailtemplateConfig {}
