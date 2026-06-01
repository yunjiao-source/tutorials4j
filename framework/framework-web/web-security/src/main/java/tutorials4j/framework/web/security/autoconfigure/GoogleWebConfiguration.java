package tutorials4j.framework.web.security.autoconfigure;

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
import tutorials4j.framework.web.security.google.GoogleAuthRequestFilter;
import tutorials4j.framework.web.security.google.GoogleAuthenticatorConfigCustomizer;
import tutorials4j.framework.web.security.google.TotpAuthController;
import tutorials4j.framework.web.security.google.TotpAuthService;
import tutorials4j.framework.web.security.google.YamlCredentialRepository;
import tutorials4j.framework.web.security.properties.GoogleWebProperties;

/**
 * Google Authenticator 自动配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnClass(GoogleAuthenticator.class)
@Configuration(proxyBeanMethods = false)
public class GoogleWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-SECURITY] Web Google Configuration");
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

    log.debug("[WEB-SECURITY] Google Authenticator");
    return authenticator;
  }

  @Bean
  @ConditionalOnMissingBean
  ICredentialRepository yamlCredentialRepository(GoogleWebProperties properties) {
    log.debug("[WEB-SECURITY] Yaml Credential Repository");
    return new YamlCredentialRepository(properties.getCredentials());
  }

  @Bean
  @ConditionalOnMissingBean
  TotpAuthController TotpAuthController(TotpAuthService totpAuthService) {
    log.debug("[WEB-SECURITY] Totp Auth Controller");
    return new TotpAuthController(totpAuthService);
  }

  @Bean
  @ConditionalOnMissingBean
  TotpAuthService googleAuthService(
      GoogleAuthenticator authenticator, GoogleWebProperties properties) {
    log.debug("[WEB-SECURITY] Google Auth Service");
    return new TotpAuthService(authenticator, properties.getOtpAuthTotpURL());
  }

  @Bean
  FilterRegistrationBean<GoogleAuthRequestFilter> googleAuthRequestFilterRegistration(
      TotpAuthService totpAuthService, GoogleWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<GoogleAuthRequestFilter> registration = new FilterRegistrationBean<>();
    GoogleAuthRequestFilter filter = new GoogleAuthRequestFilter(totpAuthService);
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[WEB-SECURITY] Google Auth 校验过滤器：{}", options);
    }
    return registration;
  }
}
