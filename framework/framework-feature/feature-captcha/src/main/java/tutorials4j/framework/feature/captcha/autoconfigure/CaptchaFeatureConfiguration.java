package tutorials4j.framework.feature.captcha.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.feature.captcha.web.CaptchaEndpoint;
import tutorials4j.framework.feature.captcha.web.TianaiCaptchaEndpoint;

/**
 * Web配置
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class CaptchaFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE-CAPTCHA] Captcha Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CaptchaEndpoint uniformCaptchaEndpoint(CaptchaServiceFactory factory) {
    log.debug("[FEATURE-CAPTCHA] Uniform Captcha Endpoint");
    return new CaptchaEndpoint(factory);
  }

  @Bean
  @ConditionalOnMissingBean
  TianaiCaptchaEndpoint tianaiCaptchaEndpoint(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.debug("[FEATURE-CAPTCHA] Tianai Captcha Endpoint");
    return new TianaiCaptchaEndpoint(factory, imageCaptchaApplication);
  }
}
