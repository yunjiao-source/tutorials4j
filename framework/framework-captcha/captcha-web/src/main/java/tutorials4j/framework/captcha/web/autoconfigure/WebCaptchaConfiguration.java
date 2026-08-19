package tutorials4j.framework.captcha.web.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
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
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;
import tutorials4j.framework.captcha.web.endpoint.TianaiCaptchaEndpoint;
import tutorials4j.framework.captcha.web.endpoint.UnifiedCaptchaEndpoint;
import tutorials4j.framework.captcha.web.interceptor.CaptchaAuthHandlerInterceptor;
import tutorials4j.framework.captcha.web.properties.WebCaptchaProperties;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * 验证码 Web 自动配置类。
 *
 * <p>当 {@code tutorials4j.captcha.web.enabled} 配置为 {@code true} 时生效， 注册统一验证码端点与天意验证码端点，并通过 {@link
 * CaptchaAuthWebMvcConfigurer} 注册验证码认证拦截器。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_CAPTCHA_WEB,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(WebCaptchaProperties.class)
public class WebCaptchaConfiguration {
  /** 初始化：输出 Web 验证码配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CAPTCHA-WEB] Web Captcha Configuration");
  }

  /**
   * 注册统一验证码端点。
   *
   * @param factory 验证码服务工厂
   * @return 统一验证码端点
   */
  @Bean
  @ConditionalOnMissingBean
  UnifiedCaptchaEndpoint unifiedCaptchaEndpoint(CaptchaServiceFactory factory) {
    log.trace("[CAPTCHA-WEB] Unified Captcha Endpoint");
    return new UnifiedCaptchaEndpoint(factory);
  }

  /**
   * 注册天意验证码端点。
   *
   * @param factory 验证码服务工厂
   * @param imageCaptchaApplication 天意图形验证码应用
   * @return 天意验证码端点
   */
  @Bean
  @ConditionalOnMissingBean
  TianaiCaptchaEndpoint tianaiCaptchaEndpoint(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.trace("[CAPTCHA-WEB] Tianai Captcha Endpoint");
    return new TianaiCaptchaEndpoint(factory, imageCaptchaApplication);
  }

  /**
   * 验证码认证 WebMvc 配置。
   *
   * <p>注册 {@link CaptchaAuthHandlerInterceptor}，并应用配置的包含与排除路径。
   */
  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class CaptchaAuthWebMvcConfigurer implements WebMvcConfigurer {
    private final CaptchaServiceFactory captchaServiceFactory;
    private final WebCaptchaProperties properties;

    /**
     * 注册验证码认证拦截器并应用包含/排除路径配置。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      CaptchaAuthHandlerInterceptor captchaAuthHandlerInterceptor =
          new CaptchaAuthHandlerInterceptor(captchaServiceFactory);

      InterceptorRegistration registration = registry.addInterceptor(captchaAuthHandlerInterceptor);
      HandlerInterceptorOptions options = properties.getInterceptor();
      registration.excludePathPatterns(options.getExcludePathPatterns());
      registration.addPathPatterns(options.getIncludePathPatterns());

      log.trace(
          "[WEB-SECURITY] 'CaptchaAuthHandlerInterceptor' configuration parameters are {}",
          options);
    }
  }
}
