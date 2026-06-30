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
import tutorials4j.framework.feature.signin.service.SignInService;

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
  SignInEndpoint signInEndpoint(SignInService signInService) {
    log.trace("[FEATURE-SIGN-IN] Sign In Endpoint");
    return new SignInEndpoint(signInService);
  }

  @Bean
  @ConditionalOnMissingBean
  SignInService signInService(
      SignInFeatureProperties properties,
      ObjectProvider<SignInResultHandler> signInResultHandlers) {
    log.trace("[FEATURE-SIGN-IN] Sign In Service");
    return new SignInService(signInResultHandlers.stream().sorted().toList(), properties);
  }
}
