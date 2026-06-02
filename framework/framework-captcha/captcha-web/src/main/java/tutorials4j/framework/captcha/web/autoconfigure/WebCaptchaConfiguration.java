package tutorials4j.framework.captcha.web.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.properties.CaptchaProperties;
import tutorials4j.framework.captcha.web.CaptchaRequestFilter;
import tutorials4j.framework.captcha.web.TianaiCaptchaEndpoint;
import tutorials4j.framework.captcha.web.UniformCaptchaEndpoint;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * Web配置
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class WebCaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CAPTCHA-WEB] Captcha Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  UniformCaptchaEndpoint uniformCaptchaEndpoint(CaptchaServiceFactory factory) {
    log.debug("[CAPTCHA-WEB] Uniform Captcha Endpoint");
    return new UniformCaptchaEndpoint(factory);
  }

  @Bean
  FilterRegistrationBean<CaptchaRequestFilter> captchaRequestFilterRegistration(
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
  TianaiCaptchaEndpoint tianaiCaptchaEndpoint(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.debug("[CAPTCHA-WEB] Tianai Captcha Endpoint");
    return new TianaiCaptchaEndpoint(factory, imageCaptchaApplication);
  }
}
