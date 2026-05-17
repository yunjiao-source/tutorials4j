package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * web http 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({
  FilterWebConfiguration.class,
  InterceptorWebConfiguration.class,
  SpringdocWebConfiguration.class
})
public class MvcWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-MVC] Web Mvc Configuration");
  }
}
