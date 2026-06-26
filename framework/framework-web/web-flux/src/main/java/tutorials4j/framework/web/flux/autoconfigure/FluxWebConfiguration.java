package tutorials4j.framework.web.flux.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.flux.component.GlobalWebFluxExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class FluxWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-CLIENT] Flux Web Configuration");
  }

  @Bean
  GlobalWebFluxExceptionHandler globalWebFluxValidationExceptionHandler() {
    log.trace("[WEB-CLIENT] Global Web Flux Exception Handler");
    return new GlobalWebFluxExceptionHandler();
  }
}
