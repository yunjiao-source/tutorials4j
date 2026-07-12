package tutorials4j.framework.examples.message.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 组合缓存应用配置
 *
 * @author Yun Jiao
 */
@EnableScheduling
@Configuration
@Profile("redis-integration")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.message.redis.integration"})
public class RedisIntegrationConfig {}
