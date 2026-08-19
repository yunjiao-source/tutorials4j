package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 请求日志记录示例模块的配置类，仅在 request-logging profile 下生效，负责扫描请求日志示例相关组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("request-logging")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.requestlogging"})
public class RequestLoggingConfig {}
