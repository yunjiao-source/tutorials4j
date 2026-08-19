package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 天爱验证码示例配置类。
 *
 * <p>在 {@code tianai} profile 下启用，扫描并装配天爱验证码示例包中的组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("tianai")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.tianai"})
public class TianaiConfig {}
