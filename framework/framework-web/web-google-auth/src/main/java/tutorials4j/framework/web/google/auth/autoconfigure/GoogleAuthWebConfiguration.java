package tutorials4j.framework.web.google.auth.autoconfigure;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.core.properties.GoogleAuthWebProperties;
import tutorials4j.framework.web.google.auth.GoogleAuthService;
import tutorials4j.framework.web.google.auth.GoogleAuthenticatorConfigCustomizer;
import tutorials4j.framework.web.google.auth.YamlCredentialRepository;
import tutorials4j.framework.web.google.auth.web.GoogleAuthRequestFilter;

/**
 * Google Authenticator 自动配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnClass(GoogleAuthenticator.class)
@Configuration(proxyBeanMethods = false)
public class GoogleAuthWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-GOOGLE-AUTH] Google Auth Web Configuration");
  }

  @Bean
  GoogleAuthenticator googleAuthenticator(
      ICredentialRepository repository,
      ObjectProvider<GoogleAuthenticatorConfigCustomizer> customizers) {
    // 创建配置
    GoogleAuthenticatorConfig config =
        new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build();
    customizers.orderedStream().forEach(customizer -> customizer.customize(config));

    GoogleAuthenticator authenticator = new GoogleAuthenticator(config);
    // 关键：设置我们自定义的凭证仓库
    authenticator.setCredentialRepository(repository);

    log.debug("[WEB-GOOGLE-AUTH] Google Authenticator");
    return authenticator;
  }

  @Bean
  @ConditionalOnMissingBean
  ICredentialRepository yamlCredentialRepository(GoogleAuthWebProperties properties) {
    log.debug("[WEB-GOOGLE-AUTH] Yaml Credential Repository");
    return new YamlCredentialRepository(properties.getCredentials());
  }

  @Bean
  @ConditionalOnMissingBean
  GoogleAuthService googleAuthService(
      GoogleAuthenticator authenticator, GoogleAuthWebProperties properties) {
    log.debug("[WEB-GOOGLE-AUTH] Google Auth Service");
    return new GoogleAuthService(authenticator, properties.getOtpAuthTotpURL());
  }

  @Bean
  FilterRegistrationBean<GoogleAuthRequestFilter> googleAuthRequestFilterRegistration(
      GoogleAuthService googleAuthService, GoogleAuthWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<GoogleAuthRequestFilter> registration = new FilterRegistrationBean<>();
    GoogleAuthRequestFilter filter = new GoogleAuthRequestFilter(googleAuthService);
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[WEB-GOOGLE-AUTH] Google Auth 校验过滤器：{}", options);
    }
    return registration;
  }
}
