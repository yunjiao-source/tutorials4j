package tutorials4j.framework.web.security.autoconfigure;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.security.properties.TotpWebProperties;
import tutorials4j.framework.web.security.properties.TotpWebProperties.AuthenticatorOptions;
import tutorials4j.framework.web.security.totp.GoogleAuthRequestFilter;
import tutorials4j.framework.web.security.totp.GoogleAuthService;
import tutorials4j.framework.web.security.totp.GoogleAuthenticatorConfigCustomizer;
import tutorials4j.framework.web.security.totp.GoogleYamlCredentialRepository;

/**
 * Totp Authenticator 自动配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_TOTP,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(TotpWebProperties.class)
public class TotpWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Web Google Configuration");
  }

  @Bean
  GoogleAuthenticator googleAuthenticator(
      TotpWebProperties properties,
      ICredentialRepository repository,
      ObjectProvider<GoogleAuthenticatorConfigCustomizer> customizers) {
    AuthenticatorOptions options = properties.getAuthenticator();
    // 创建配置
    GoogleAuthenticatorConfig config =
        new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
            .setTimeStepSizeInMillis(options.getTimeStepSizeInMillis())
            .setWindowSize(options.getWindowSize())
            .setCodeDigits(options.getCodeDigits())
            .setNumberOfScratchCodes(options.getNumberOfScratchCodes())
            .setSecretBits(options.getSecretBits())
            .build();
    customizers.orderedStream().forEach(customizer -> customizer.customize(config));

    GoogleAuthenticator authenticator = new GoogleAuthenticator(config);
    // 关键：设置我们自定义的凭证仓库
    authenticator.setCredentialRepository(repository);

    log.trace("[WEB-SECURITY] Google Authenticator");
    return authenticator;
  }

  @Bean
  @ConditionalOnMissingBean
  ICredentialRepository yamlCredentialRepository(TotpWebProperties properties) {
    log.trace("[WEB-SECURITY] Yaml Credential Repository");
    return new GoogleYamlCredentialRepository(properties.getCredentials());
  }

  @Bean
  @ConditionalOnMissingBean
  GoogleAuthService googleAuthService(
      GoogleAuthenticator authenticator, TotpWebProperties properties) {
    log.trace("[WEB-SECURITY] Google Auth Service");
    return new GoogleAuthService(authenticator, properties.getOtpAuthTotpURL());
  }

  @Bean
  FilterRegistrationBean<GoogleAuthRequestFilter> googleAuthRequestFilterRegistration(
      GoogleAuthService googleAuthService, TotpWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<GoogleAuthRequestFilter> registration = new FilterRegistrationBean<>();
    GoogleAuthRequestFilter filter = new GoogleAuthRequestFilter(googleAuthService);
    registration.setFilter(filter);
    options.fill(registration);

    log.trace("[WEB-SECURITY] GoogleAuthRequestFilter configuration parameters are {}", options);
    return registration;
  }
}
