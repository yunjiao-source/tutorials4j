package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 组合任务装饰器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("exception")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.exception"})
public class ExceptionConfig {}
