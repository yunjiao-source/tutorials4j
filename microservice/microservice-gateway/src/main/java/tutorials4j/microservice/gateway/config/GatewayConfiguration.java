package tutorials4j.microservice.gateway.config;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import tutorials4j.microservice.gateway.component.SentinelExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
public class GatewayConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[MICROSERVICE-GATEWAY] Gateway Microservice Configuration");
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  SentinelExceptionHandler sentinelExceptionHandler(Tracer tracer) {
    log.trace("[MICROSERVICE-GATEWAY] Sentinel Exception Handler");
    return new SentinelExceptionHandler(tracer);
  }
}
