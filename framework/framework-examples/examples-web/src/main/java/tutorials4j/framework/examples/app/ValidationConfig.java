package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 参数校验示例模块的配置类，仅在 validation profile 下生效，负责扫描参数校验示例相关组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("validation")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.validation"})
public class ValidationConfig {}
