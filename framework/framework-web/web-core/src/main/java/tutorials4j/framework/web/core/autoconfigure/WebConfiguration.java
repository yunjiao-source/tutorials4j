package tutorials4j.framework.web.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.component.GlobalWebExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-CORE] Web Core Configuration");
  }

  @Bean
  GlobalWebExceptionHandler globalWebExceptionHandler() {
    log.trace("[WEB-CORE] Global Web Exception Handler");
    return new GlobalWebExceptionHandler();
  }
}
