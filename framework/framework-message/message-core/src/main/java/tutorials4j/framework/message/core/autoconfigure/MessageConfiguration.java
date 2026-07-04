package tutorials4j.framework.message.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.message.core.properties.MessageProperties;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({MessageProperties.class})
public class MessageConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE-CORE] Message Configuration");
  }
}
