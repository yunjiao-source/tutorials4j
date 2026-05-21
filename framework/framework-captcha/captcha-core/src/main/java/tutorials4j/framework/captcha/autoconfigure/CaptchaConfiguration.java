package tutorials4j.framework.captcha.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.GraphicCaptchaCacheTemplate;
import tutorials4j.framework.captcha.properties.CaptchaProperties;
import tutorials4j.framework.captcha.properties.HutoolCaptchaProperties;
import tutorials4j.framework.captcha.properties.TianaiCaptchaProperties;
import tutorials4j.framework.captcha.web.CaptchaController;
import tutorials4j.framework.captcha.web.CaptchaRequestFilter;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  CaptchaProperties.class,
  HutoolCaptchaProperties.class,
  TianaiCaptchaProperties.class
})
public class CaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CAPTCHA-CORE] Captcha Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CaptchaController CaptchaController(CaptchaServiceFactory factory) {
    log.debug("[CAPTCHA-CORE] Captcha Controller");
    return new CaptchaController(factory);
  }

  @Bean
  @ConditionalOnMissingBean
  BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate() {
    log.debug("[CAPTCHA-CORE] Behavior Captcha Cache Template");
    return new BehaviorCaptchaCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  GraphicCaptchaCacheTemplate graphicCaptchaCacheTemplate() {
    log.debug("[CAPTCHA-CORE] Graphic Captcha Cache Template");
    return new GraphicCaptchaCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  CaptchaServiceFactory captchaServiceFactory(ObjectProvider<CaptchaService> captchaServices) {
    Map<CaptchaCategory, CaptchaService> services =
        captchaServices.stream().collect(Collectors.toMap(CaptchaService::getCategory, m -> m));
    log.debug("[CAPTCHA-CORE] 工厂'CaptchaServiceFactory'注入实例：{}", services);
    return new CaptchaServiceFactory(services);
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
      log.debug("[CAPTCHA-CORE] 验证码校验过滤器：{}", options);
    }
    return registration;
  }
}
