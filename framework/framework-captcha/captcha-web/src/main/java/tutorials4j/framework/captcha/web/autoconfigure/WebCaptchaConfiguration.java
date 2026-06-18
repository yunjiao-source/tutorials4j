package tutorials4j.framework.captcha.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;
import tutorials4j.framework.captcha.web.filter.CaptchaRequestFilter;
import tutorials4j.framework.captcha.web.properties.WebCaptchaProperties;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

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

  @Bean
  FilterRegistrationBean<CaptchaRequestFilter> captchaRequestFilterRegistration(
      CaptchaServiceFactory captchaServiceFactory, WebCaptchaProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<CaptchaRequestFilter> registration = new FilterRegistrationBean<>();
    CaptchaRequestFilter filter = new CaptchaRequestFilter(captchaServiceFactory);
    registration.setFilter(filter);
    options.fill(registration);

    log.trace("[CAPTCHA-CORE] CaptchaRequestFilter configuration parameters are {}", options);
    return registration;
  }
}
