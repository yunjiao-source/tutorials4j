package tutorials4j.service.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.session.autoconfigure.SessionServiceDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.service.ai.session.AuthorizingSessionService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SessionServiceConfiguration {
  @Bean
  public SessionServiceDecorator sessionServiceDecorator() {
    return AuthorizingSessionService::new;
  }
}
