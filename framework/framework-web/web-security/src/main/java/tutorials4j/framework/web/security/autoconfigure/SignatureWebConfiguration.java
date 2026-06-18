package tutorials4j.framework.web.security.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tutorials4j.framework.web.security.properties.SignatureWebProperties;
import tutorials4j.framework.web.security.signature.InMemerySignatureKeyRepository;
import tutorials4j.framework.web.security.signature.SignatureHandlerInterceptor;
import tutorials4j.framework.web.security.signature.SignatureKeyRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_SIGNATURE,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(SignatureWebProperties.class)
public class SignatureWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Signature Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SignatureKeyRepository inMemerySignatureKeyRepository(SignatureWebProperties properties) {
    log.trace("[WEB-SECURITY] In Memery Signature Key Repository");
    return new InMemerySignatureKeyRepository(properties);
  }

  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class SignatureWebMvcConfigurer implements WebMvcConfigurer {
    private final SignatureKeyRepository signatureKeyRepository;
    private final SignatureWebProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      SignatureHandlerInterceptor signatureHandlerInterceptor =
          new SignatureHandlerInterceptor(
              properties.getNonceRedisKeyPrefix(), signatureKeyRepository);

      InterceptorRegistration registration = registry.addInterceptor(signatureHandlerInterceptor);
      HandlerInterceptorOptions options = properties.getInterceptor();
      if (options.getExcludePathPatterns().length > 0) {
        registration.excludePathPatterns(options.getExcludePathPatterns());
      }

      if (options.getIncludePathPatterns().length > 0) {
        registration.addPathPatterns(options.getIncludePathPatterns());
      }

      log.trace("[WEB-SECURITY] 添加请求拦截器：{}, {}", signatureHandlerInterceptor, options);
    }
  }
}
