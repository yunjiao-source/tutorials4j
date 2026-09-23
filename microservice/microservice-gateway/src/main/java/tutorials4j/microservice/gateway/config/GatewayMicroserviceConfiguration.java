package tutorials4j.microservice.gateway.config;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tutorials4j.microservice.gateway.sentinel.SentinelExceptionHandler;
import tutorials4j.toolkit.satoken.strategy.CorsBeforeSaFilterAuthStrategy;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
public class GatewayMicroserviceConfiguration {
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

  @Bean
  PasswordEncoder bcryptPasswordEncoder() {
    log.trace("[MICROSERVICE-GATEWAY] BCrypt Password Encoder");
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsBeforeSaFilterAuthStrategy corsBeforeSaFilterAuthStrategy() {
    log.trace("[MICROSERVICE-GATEWAY] Cors Before Sa Filter Auth Strategy");
    return new CorsBeforeSaFilterAuthStrategy();
  }
}
