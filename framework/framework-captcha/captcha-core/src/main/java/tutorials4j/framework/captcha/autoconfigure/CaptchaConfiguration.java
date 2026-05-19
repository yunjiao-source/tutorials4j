package tutorials4j.framework.captcha.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.captcha.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.properties.HutoolCaptchaProperties;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({HutoolCaptchaProperties.class})
public class CaptchaConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CAPTCHA-CORE] Captcha Configuration");
  }

  @Bean
  BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate() {
    log.debug("[CAPTCHA-CORE] Behavior Captcha Cache Template");
    return new BehaviorCaptchaCacheTemplate();
  }

  @Bean
  CaptchaServiceFactory captchaServiceFactory(ObjectProvider<CaptchaService> captchaServices) {
    Map<CaptchaCategory, CaptchaService> services =
        captchaServices.stream().collect(Collectors.toMap(CaptchaService::getCategory, m -> m));
    log.debug("[CACHE-CORE] 工厂'CaptchaServiceFactory'注入实例：{}", services);
    return new CaptchaServiceFactory(services);
  }
}
