package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.core.autoconfigure.WebConfiguration;
import tutorials4j.framework.web.google.auth.autoconfigure.GoogleAuthWebConfiguration;
import tutorials4j.framework.web.mvc.autoconfigure.MvcWebConfiguration;
import tutorials4j.framework.web.rest.autoconfigure.RestWebConfiguration;

/**
 * 缓存请求体自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  WebConfiguration.class,
  GoogleAuthWebConfiguration.class,
  RestWebConfiguration.class,
  MvcWebConfiguration.class
})
public class WebAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB] Web Auto Configuration");
  }
}
