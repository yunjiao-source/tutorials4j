package tutorials4j.framework.oauth.sotoken.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.oauth.sotoken.component.SaApiKeyTemplateFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SoTokenOAuthConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH-SO-TOKEN] So-Token Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SaApiKeyTemplateFactory saApiKeyTemplateFactory() {
    log.trace("[OAUTH-SO-TOKEN] Sa Api Key Template Factory");
    return new SaApiKeyTemplateFactory();
  }
}
