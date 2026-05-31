package tutorials4j.framework.captcha.web.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.properties.CaptchaProperties;
import tutorials4j.framework.captcha.web.CaptchaRequestFilter;
import tutorials4j.framework.captcha.web.TianaiCaptchaController;
import tutorials4j.framework.captcha.web.UniformCaptchaController;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebCaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CAPTCHA-WEB] Captcha Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  UniformCaptchaController CaptchaController(CaptchaServiceFactory factory) {
    log.debug("[CAPTCHA-WEB] Captcha Controller");
    return new UniformCaptchaController(factory);
  }

  @Bean
  FilterRegistrationBean<CaptchaRequestFilter> traceRequestFilterRegistration(
      CaptchaServiceFactory captchaServiceFactory, CaptchaProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<CaptchaRequestFilter> registration = new FilterRegistrationBean<>();
    CaptchaRequestFilter filter = new CaptchaRequestFilter(captchaServiceFactory);
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[CAPTCHA-WEB] 验证码校验过滤器：{}", options);
    }
    return registration;
  }

  @Bean
  @ConditionalOnMissingBean
  TianaiCaptchaController tianaiCaptchaController(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.debug("[CAPTCHA-WEB] Tianai Captcha Controller");
    return new TianaiCaptchaController(factory, imageCaptchaApplication);
  }
}
