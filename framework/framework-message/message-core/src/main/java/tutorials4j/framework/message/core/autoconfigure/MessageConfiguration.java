package tutorials4j.framework.message.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;

/**
 * 消息模块核心自动配置类。
 *
 * <p>启用消息模块配置属性（{@link MessageProperties}）的绑定，作为消息模块的配置入口。
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnMissingBean({StreamBridge.class})
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({MessageProperties.class})
public class MessageConfiguration {
  /** 初始化：输出消息核心配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[MESSAGE-CORE] Message Configuration");
  }
}
