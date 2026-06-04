package tutorials4j.framework.message.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({})
public class MessageAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[MESSAGE] Message Auto Configuration");
  }
}
