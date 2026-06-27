package tutorials4j.framework.captcha.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;
import tutorials4j.framework.captcha.web.component.CaptchaAuthHandlerInterceptor;
import tutorials4j.framework.captcha.web.properties.WebCaptchaProperties;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * Web配置
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
  @PostConstruct
  public void postConstruct() {
    log.trace("[CAPTCHA-WEB] Web Captcha Configuration");
  }

  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class CaptchaAuthWebMvcConfigurer implements WebMvcConfigurer {
    private final CaptchaServiceFactory captchaServiceFactory;
    private final WebCaptchaProperties properties;

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
