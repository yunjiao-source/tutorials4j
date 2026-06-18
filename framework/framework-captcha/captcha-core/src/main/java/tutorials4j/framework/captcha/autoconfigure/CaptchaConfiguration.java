package tutorials4j.framework.captcha.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.support.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.support.CaptchaCategory;
import tutorials4j.framework.captcha.support.CaptchaService;
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;
import tutorials4j.framework.captcha.support.GraphicCaptchaCacheTemplate;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[CAPTCHA-CORE] Captcha Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate() {
    log.trace("[CAPTCHA-CORE] Behavior Captcha Cache Template");
    return new BehaviorCaptchaCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  GraphicCaptchaCacheTemplate graphicCaptchaCacheTemplate() {
    log.trace("[CAPTCHA-CORE] Graphic Captcha Cache Template");
    return new GraphicCaptchaCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  CaptchaServiceFactory captchaServiceFactory(ObjectProvider<CaptchaService> providers) {
    Map<CaptchaCategory, CaptchaService> services =
        providers.stream().collect(Collectors.toMap(CaptchaService::getCategory, m -> m));
    log.trace("[CAPTCHA-CORE] 工厂'CaptchaServiceFactory'注入实例：{}", services);
    CaptchaServiceFactory.instance.setServices(services);
    return CaptchaServiceFactory.instance;
  }
}
