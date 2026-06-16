package tutorials4j.framework.captcha.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.properties.CaptchaProperties;
import tutorials4j.framework.captcha.web.CaptchaRequestFilter;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * Web配置
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
  FilterRegistrationBean<CaptchaRequestFilter> captchaRequestFilterRegistration(
      CaptchaServiceFactory captchaServiceFactory, CaptchaProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<CaptchaRequestFilter> registration = new FilterRegistrationBean<>();
    CaptchaRequestFilter filter = new CaptchaRequestFilter(captchaServiceFactory);
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[CAPTCHA-CORE] 验证码校验过滤器：{}", options);
    }
    return registration;
  }
}
