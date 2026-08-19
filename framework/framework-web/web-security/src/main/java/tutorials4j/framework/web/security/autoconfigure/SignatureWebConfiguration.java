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
 * 接口签名校验功能自动配置类，在属性开启时注册签名密钥仓库与签名校验拦截器。
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
  /** 配置初始化完成后输出跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Signature Web Configuration");
  }

  /** 注册基于内存 Map 的签名密钥仓库 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  SignatureKeyRepository inMemerySignatureKeyRepository(SignatureWebProperties properties) {
    log.trace("[WEB-SECURITY] In Memery Signature Key Repository");
    return new InMemerySignatureKeyRepository(properties);
  }

  /** 签名校验拦截器注册配置，按属性配置将签名校验拦截器添加到注册表。 */
  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class SignatureWebMvcConfigurer implements WebMvcConfigurer {
    private final SignatureKeyRepository signatureKeyRepository;
    private final SignatureWebProperties properties;

    /**
     * 注册签名校验拦截器，并应用配置中的排除与包含路径规则。
     *
     * @param registry 拦截器注册表
     */
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

      log.trace(
          "[WEB-SECURITY] 'SignatureHandlerInterceptor' configuration parameters are {}", options);
    }
  }
}
