package tutorials4j.framework.feature.captcha.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.feature.captcha.web.CaptchaEndpoint;
import tutorials4j.framework.feature.captcha.web.CaptchaTianaiEndpoint;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"tutorials4j.framework.feature.captcha.web"})
@ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE, name = "captcha-enabled")
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
  CaptchaTianaiEndpoint captchaTianaiEndpoint(
      CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
    log.debug("[FEATURE-CAPTCHA] Captcha Tianai Endpoint");
    return new CaptchaTianaiEndpoint(factory, imageCaptchaApplication);
  }
}
