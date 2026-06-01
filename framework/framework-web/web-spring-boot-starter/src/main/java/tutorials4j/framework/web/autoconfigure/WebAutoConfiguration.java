package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.client.autoconfigure.ClientWebConfiguration;
import tutorials4j.framework.web.logging.autoconfigure.LoggingWebConfiguration;
import tutorials4j.framework.web.logging.autoconfigure.SpringdocWebConfiguration;
import tutorials4j.framework.web.rest.autoconfigure.RestWebConfiguration;
import tutorials4j.framework.web.security.autoconfigure.GoogleWebConfiguration;
import tutorials4j.framework.web.security.autoconfigure.SecurityWebConfiguration;
import tutorials4j.framework.web.validation.autoconfigure.ValidatorsWebConfiguration;

/**
 * 缓存请求体自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  ClientWebConfiguration.class,
  LoggingWebConfiguration.class,
  SpringdocWebConfiguration.class,
  RestWebConfiguration.class,
  SecurityWebConfiguration.class,
  GoogleWebConfiguration.class,
  ValidatorsWebConfiguration.class
})
public class WebAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB] Web Auto Configuration");
  }
}
