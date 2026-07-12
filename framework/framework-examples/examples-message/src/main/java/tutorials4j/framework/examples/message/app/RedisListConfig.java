package tutorials4j.framework.examples.message.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 组合缓存应用配置
 *
 * @author Yun Jiao
 */
@Configuration
@Profile("redis-list")
@ComponentScan(
    basePackages = {
      "tutorials4j.framework.examples.message.redis.list",
      "tutorials4j.framework.examples.message.redis.event"
    })
public class RedisListConfig {}
