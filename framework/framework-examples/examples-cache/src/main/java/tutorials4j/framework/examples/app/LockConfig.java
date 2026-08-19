package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Redisson 分布式锁示例配置，仅在 {@code lock} Profile 下生效。
 *
 * <p>扫描分布式锁示例包 {@code tutorials4j.framework.examples.lock} 中的组件。
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("lock")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.lock"})
public class LockConfig {}
