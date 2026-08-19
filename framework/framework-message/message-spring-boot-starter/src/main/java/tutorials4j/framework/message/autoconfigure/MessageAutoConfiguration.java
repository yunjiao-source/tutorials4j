package tutorials4j.framework.message.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.message.core.autoconfigure.MessageConfiguration;
import tutorials4j.framework.message.redis.autoconfigure.RedisMessageConfiguration;

/**
 * 消息模块自动配置入口类。
 *
 * <p>汇总导入消息核心配置与 Redis 消息配置，供 Spring Boot 自动装配加载。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({MessageConfiguration.class, RedisMessageConfiguration.class})
public class MessageAutoConfiguration {
  /** 初始化：输出消息模块自动配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE] Message Auto Configuration");
  }
}
