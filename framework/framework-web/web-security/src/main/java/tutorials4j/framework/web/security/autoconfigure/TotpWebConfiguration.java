package tutorials4j.framework.web.security.autoconfigure;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.ICredentialRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.web.security.properties.TotpWebProperties;
import tutorials4j.framework.web.security.properties.TotpWebProperties.AuthenticatorOptions;
import tutorials4j.framework.web.security.totp.GoogleAuthService;
import tutorials4j.framework.web.security.totp.GoogleAuthenticatorConfigCustomizer;
import tutorials4j.framework.web.security.totp.GoogleYamlCredentialRepository;
import tutorials4j.framework.web.security.totp.TotpAuthEndpoint;
import tutorials4j.framework.web.security.totp.TotpAuthHandlerInterceptor;

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
  /** 配置初始化完成后输出跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Totp Web Configuration");
  }

  /**
   * 根据配置构建 GoogleAuthenticator Bean，并挂载自定义凭证仓库。
   *
   * @param properties TOTP 相关配置属性
   * @param repository 凭证仓库
   * @param customizers 配置定制器（可为空）
   * @return GoogleAuthenticator 实例
   */
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

  /** 注册基于 YAML 配置的凭证仓库 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  ICredentialRepository yamlCredentialRepository(TotpWebProperties properties) {
    log.trace("[WEB-SECURITY] Yaml Credential Repository");
    return new GoogleYamlCredentialRepository(properties.getCredentials());
  }

  /** 注册 TOTP 认证服务 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  GoogleAuthService googleAuthService(
      GoogleAuthenticator authenticator, TotpWebProperties properties) {
    log.trace("[WEB-SECURITY] Google Auth Service");
    return new GoogleAuthService(authenticator, properties.getOtpAuthTotpURL());
  }

  /** 注册 TOTP 认证拦截器 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  TotpAuthHandlerInterceptor totpAuthHandlerInterceptor(GoogleAuthService googleAuthService) {
    log.trace("[WEB-SECURITY] Totp Auth Handler Interceptor");
    return new TotpAuthHandlerInterceptor(googleAuthService);
  }

  /** 注册 TOTP 认证端点 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  TotpAuthEndpoint totpAuthEndpoint(GoogleAuthService googleAuthService) {
    log.trace("[WEB-SECURITY] Totp Auth Endpoint");
    return new TotpAuthEndpoint(googleAuthService);
  }

  /** TOTP 认证拦截器注册配置，按属性配置将认证拦截器添加到注册表。 */
  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class TotpAuthWebMvcConfigurer implements WebMvcConfigurer {
    private final GoogleAuthService googleAuthService;
    private final TotpWebProperties properties;

    /**
     * 注册 TOTP 认证拦截器，并应用配置中的排除与包含路径规则。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      TotpAuthHandlerInterceptor totpAuthHandlerInterceptor =
          new TotpAuthHandlerInterceptor(googleAuthService);

      InterceptorRegistration registration = registry.addInterceptor(totpAuthHandlerInterceptor);
      HandlerInterceptorOptions options = properties.getInterceptor();
      registration.excludePathPatterns(options.getExcludePathPatterns());
      registration.addPathPatterns(options.getIncludePathPatterns());

      log.trace(
          "[WEB-SECURITY] 'TotpAuthHandlerInterceptor' configuration parameters are {}", options);
    }
  }
}
