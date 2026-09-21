package tutorials4j.microservice.oauth.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tutorials4j.feature.oauth.service.UserService;
import tutorials4j.microservice.oauth.component.DefaultSaOAuth2ScopeHandlerInterface;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  OAuthMicroserviceProperties.class,
})
public class OAuthMicroserviceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[MICROSERVICE-OAUTH] OAuth Microservice Configuration");
  }

  @Bean
  PasswordEncoder bcryptPasswordEncoder() {
    log.trace("[MICROSERVICE-OAUTH] BCrypt Password Encoder");
    return new BCryptPasswordEncoder();
  }

  @Bean
  @ConditionalOnProperty(
      prefix = PropertyConsts.PROPERTY_TOOLKIT_SECURITY_SA_TOKEN,
      name = "enable-default-scope-handler-interface")
  DefaultSaOAuth2ScopeHandlerInterface defaultSaOAuth2ScopeHandlerInterface(
      UserService userService) {
    log.trace("[MICROSERVICE-OAUTH] Default Sa OAuth2 Scope Handler Interface");
    return new DefaultSaOAuth2ScopeHandlerInterface(userService);
  }
}
