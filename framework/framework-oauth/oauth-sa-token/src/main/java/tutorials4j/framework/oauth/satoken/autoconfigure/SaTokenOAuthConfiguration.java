package tutorials4j.framework.oauth.satoken.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.oauth.satoken.component.SaApiKeyTemplateFactory;
import tutorials4j.framework.oauth.satoken.component.SaTokenExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SaTokenOAuthConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH-SA-TOKEN] Sa-Token Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SaApiKeyTemplateFactory saApiKeyTemplateFactory() {
    log.trace("[OAUTH-SA-TOKEN] Sa Api Key Template Factory");
    return new SaApiKeyTemplateFactory();
  }

  @Bean
  SaTokenExceptionHandler saTokenExceptionHandler() {
    log.trace("[OAUTH-SA-TOKEN] Sa Token Exception Handler");
    return new SaTokenExceptionHandler();
  }
}
