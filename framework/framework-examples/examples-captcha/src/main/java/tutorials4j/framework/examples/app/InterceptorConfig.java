package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 拦截器示例配置类。
 *
 * <p>在 {@code interceptor} profile 下启用，扫描并装配拦截器示例包中的组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("interceptor")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.interceptor"})
public class InterceptorConfig {}
