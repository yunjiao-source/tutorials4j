package tutorials4j.framework.captcha.web.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.web.CaptchaEndpoint;
import tutorials4j.framework.captcha.web.TianaiCaptchaEndpoint;

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
  @ConditionalOnMissingBean
  CaptchaEndpoint uniformCaptchaEndpoint(CaptchaServiceFactory factory) {
    log.debug("[CAPTCHA-WEB] Uniform Captcha Endpoint");
    return new CaptchaEndpoint(factory);
  }

  @Bean
  @ConditionalOnMissingBean
  TianaiCaptchaEndpoint tianaiCaptchaEndpoint(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.debug("[CAPTCHA-WEB] Tianai Captcha Endpoint");
    return new TianaiCaptchaEndpoint(factory, imageCaptchaApplication);
  }
}
