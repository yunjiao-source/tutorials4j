package tutorials4j.framework.feature.signin.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.feature.signin.properties.SignInFeatureProperties;
import tutorials4j.framework.feature.signin.service.SignInEndpoint;
import tutorials4j.framework.feature.signin.service.SignInResultHandler;
import tutorials4j.framework.feature.signin.service.SignInTemplateFactory;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SignInFeatureProperties.class})
public class SignInServiceFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SIGN-IN] Sign In Service Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SignInEndpoint signInEndpoint(SignInTemplateFactory signInTemplateFactory) {
    log.trace("[FEATURE-SIGN-IN] Sign In Endpoint");
    return new SignInEndpoint(signInTemplateFactory);
  }

  @Bean
  @ConditionalOnMissingBean
  SignInTemplateFactory signInService(
      SignInFeatureProperties properties,
      ObjectProvider<SignInResultHandler> signInResultHandlers) {
    log.trace("[FEATURE-SIGN-IN] Sign In Template Factory");
    return new SignInTemplateFactory(signInResultHandlers.stream().sorted().toList(), properties);
  }
}
