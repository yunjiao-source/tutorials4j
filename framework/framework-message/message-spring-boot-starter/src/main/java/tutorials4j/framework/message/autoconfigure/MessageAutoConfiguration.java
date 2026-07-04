package tutorials4j.framework.message.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.message.core.autoconfigure.MessageConfiguration;
import tutorials4j.framework.message.redis.autoconfigure.RedisMessageConfiguration;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({MessageConfiguration.class, RedisMessageConfiguration.class})
public class MessageAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE] Message Auto Configuration");
  }
}
