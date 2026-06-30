package tutorials4j.framework.feature.signin.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.framework.feature.signin.jpa.SignInResultEndpoint;
import tutorials4j.framework.feature.signin.jpa.SignInResultService;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"tutorials4j.framework.feature.signin.jpa"})
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.signin.jpa"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.signin.jpa"})
public class SignInJpaFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SIGN-IN] Sign In Jpa Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SignInResultEndpoint signInResultEndpoint(SignInResultService signInResultService) {
    log.trace("[FEATURE-SIGN-IN] Sign In Result End point");
    return new SignInResultEndpoint(signInResultService);
  }
}
