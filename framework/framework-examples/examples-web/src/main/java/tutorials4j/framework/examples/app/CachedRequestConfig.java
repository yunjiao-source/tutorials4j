package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 请求体缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("cached-request")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cachedrequest"})
public class CachedRequestConfig {
}
