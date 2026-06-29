package tutorials4j.framework.feature.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.feature.signin.LoggingSignInResultHandler;
import tutorials4j.framework.feature.signin.SignInFeatureProperties;
import tutorials4j.framework.feature.signin.SignInResultHandler;
import tutorials4j.framework.feature.signin.SignInService;

/**
 * 签到功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({FeatureProperties.class, SignInFeatureProperties.class})
public class FeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-CORE] Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SignInResultHandler LoggingSignInResultHandler() {
    log.trace("[FEATURE-CORE] Logging Sign In Result Handler");
    return new LoggingSignInResultHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  SignInService signInService(
      SignInResultHandler signInResultHandler, SignInFeatureProperties properties) {
    log.trace("[FEATURE-CORE] Sign In Service");
    return new SignInService(signInResultHandler, properties);
  }
}
