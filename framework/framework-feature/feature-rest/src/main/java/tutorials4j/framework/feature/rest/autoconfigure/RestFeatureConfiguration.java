package tutorials4j.framework.feature.rest.autoconfigure;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.feature.rest.captcha.CaptchaEndpoint;
import tutorials4j.framework.feature.rest.captcha.TianaiCaptchaEndpoint;

/**
 * Web配置
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class RestFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE-REST] Captcha Feature Configuration");
  }

  @Configuration(proxyBeanMethods = false)
  public class CaptchaFeatureConfiguration {
    @PostConstruct
    public void postConstruct() {
      log.debug("[FEATURE-REST] Captcha Feature Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    CaptchaEndpoint uniformCaptchaEndpoint(CaptchaServiceFactory factory) {
      log.debug("[FEATURE-REST] Uniform Captcha Endpoint");
      return new CaptchaEndpoint(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    TianaiCaptchaEndpoint tianaiCaptchaEndpoint(
        CaptchaServiceFactory factory, ImageCaptchaApplication imageCaptchaApplication) {
      log.debug("[FEATURE-REST] Tianai Captcha Endpoint");
      return new TianaiCaptchaEndpoint(factory, imageCaptchaApplication);
    }
  }
}
