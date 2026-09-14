package tutorials4j.toolkit.webflux.autoconfigure;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.webflux.GlobalWebfluxExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class WebfluxToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT - WEBFLUX] Webflux Toolkit Auto Configuration");
  }

  @Bean
  GlobalWebfluxExceptionHandler globalWebfluxExceptionHandler(Tracer tracer) {
    log.trace("[TOOLKIT - WEBFLUX] Global Webflux Exception Handler");
    return new GlobalWebfluxExceptionHandler(tracer);
  }
}
