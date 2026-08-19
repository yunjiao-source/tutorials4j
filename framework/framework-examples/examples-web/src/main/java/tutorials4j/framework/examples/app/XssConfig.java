package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * XSS 防护示例模块的配置类，仅在 xss profile 下生效，负责扫描 XSS 防护示例相关组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("xss")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.xss"})
public class XssConfig {}
