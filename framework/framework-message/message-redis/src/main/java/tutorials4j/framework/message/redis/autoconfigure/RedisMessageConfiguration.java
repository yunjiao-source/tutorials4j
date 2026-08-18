package tutorials4j.framework.message.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedisMessageConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE-REDIS] Redis Message Configuration");
  }
}
