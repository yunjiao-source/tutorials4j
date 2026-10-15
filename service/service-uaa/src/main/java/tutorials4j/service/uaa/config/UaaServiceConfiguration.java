package tutorials4j.service.uaa.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tutorials4j.feature.upms.service.UserService;
import tutorials4j.service.uaa.component.DefaultSaOAuth2ScopeHandlerInterface;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  UaaServiceProperties.class,
})
public class UaaServiceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-UPMS] Upms Service Configuration");
  }

  @Bean
  PasswordEncoder bcryptPasswordEncoder() {
    log.trace("[SERVICE-UPMS] BCrypt Password Encoder");
    return new BCryptPasswordEncoder();
  }

  @Bean
  DefaultSaOAuth2ScopeHandlerInterface defaultSaOAuth2ScopeHandlerInterface(
      UserService userService) {
    log.trace("[SERVICE-UPMS] Default Sa OAuth2 Scope Handler Interface");
    return new DefaultSaOAuth2ScopeHandlerInterface(userService);
  }
}
