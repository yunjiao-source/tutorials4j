package tutorials4j.service.gateway.config;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tutorials4j.service.gateway.sentinel.SentinelExceptionHandler;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Configuration
public class GatewayServiceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-GATEWAY] Gateway Service Configuration");
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  SentinelExceptionHandler sentinelExceptionHandler(Tracer tracer) {
    log.trace("[SERVICE-GATEWAY] Sentinel Exception Handler");
    return new SentinelExceptionHandler(tracer);
  }

  @Bean
  PasswordEncoder bcryptPasswordEncoder() {
    log.trace("[SERVICE-GATEWAY] BCrypt Password Encoder");
    return new BCryptPasswordEncoder();
  }
}
