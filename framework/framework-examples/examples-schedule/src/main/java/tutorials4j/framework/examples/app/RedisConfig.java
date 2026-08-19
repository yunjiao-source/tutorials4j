package tutorials4j.framework.examples.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Redis 定时任务示例配置，仅在 redis 环境激活时生效， 开启定时调度并扫描 Redis 示例相关组件。
 *
 * @author Yun Jiao
 */
@EnableScheduling
@Configuration
@Profile("redis")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.redis"})
public class RedisConfig {}
