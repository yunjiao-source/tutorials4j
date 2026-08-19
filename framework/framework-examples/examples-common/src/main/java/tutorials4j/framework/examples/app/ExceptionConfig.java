package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 异常处理示例配置类。
 *
 * <p>在 {@code exception} profile 下启用，扫描并装配异常处理示例包中的组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("exception")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.exception"})
public class ExceptionConfig {}
