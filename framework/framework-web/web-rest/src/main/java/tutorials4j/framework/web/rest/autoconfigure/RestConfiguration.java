package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * web client 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({
  RestClientDefaultConfiguration.class,
  RestClientLoggerConfiguration.class,
  RestTraceConfiguration.class
})
public class RestConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-REST] Web Rest Configuration");
  }
}
