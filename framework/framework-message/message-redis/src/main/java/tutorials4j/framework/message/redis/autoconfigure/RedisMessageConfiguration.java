package tutorials4j.framework.message.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 基于 Redis 的消息模块自动配置类。
 *
 * <p>作为消息模块 Redis 相关组件的配置入口，目前暂无具体 Bean 定义。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedisMessageConfiguration {

  /** 初始化：输出 Redis 消息配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE-REDIS] Redis Message Configuration");
  }
}
