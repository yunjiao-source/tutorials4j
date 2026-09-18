package tutorials4j.toolkit.webmvc.autoconfigure;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.webmvc.GlobalWebmvcExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class WebmvcToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-WEBMVC] Webmvc Toolkit Auto Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  GlobalWebmvcExceptionHandler globalWebmvcExceptionHandler(Tracer tracer) {
    log.trace("[TOOLKIT-WEBMVC] Global Webmvc Exception Handler");
    return new GlobalWebmvcExceptionHandler(tracer);
  }
}
