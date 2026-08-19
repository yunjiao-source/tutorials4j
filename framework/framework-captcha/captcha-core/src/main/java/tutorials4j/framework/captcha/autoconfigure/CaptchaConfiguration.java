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
 * 验证码核心自动配置类。
 *
 * <p>注册行为验证码缓存模板、图形验证码缓存模板，并将容器中所有 {@link CaptchaService} 汇聚到 {@link CaptchaServiceFactory}，
 * 供各验证码实现按分类查找服务。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CaptchaConfiguration {
  /** 初始化：输出验证码核心配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CAPTCHA-CORE] Captcha Configuration");
  }

  /**
   * 注册行为验证码缓存模板。
   *
   * @return 行为验证码缓存模板
   */
  @Bean
  @ConditionalOnMissingBean
  BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate() {
    log.trace("[CAPTCHA-CORE] Behavior Captcha Cache Template");
    return new BehaviorCaptchaCacheTemplate();
  }

  /**
   * 注册图形验证码缓存模板。
   *
   * @return 图形验证码缓存模板
   */
  @Bean
  @ConditionalOnMissingBean
  GraphicCaptchaCacheTemplate graphicCaptchaCacheTemplate() {
    log.trace("[CAPTCHA-CORE] Graphic Captcha Cache Template");
    return new GraphicCaptchaCacheTemplate();
  }

  /**
   * 注册验证码服务工厂，将容器中所有验证码服务按分类收集并注入工厂。
   *
   * @param providers 容器中的验证码服务提供者
   * @return 验证码服务工厂单例
   */
  @Bean
  @ConditionalOnMissingBean
  CaptchaServiceFactory captchaServiceFactory(ObjectProvider<CaptchaService> providers) {
    Map<CaptchaCategory, CaptchaService> services =
        providers.stream().collect(Collectors.toMap(CaptchaService::getCategory, m -> m));
    log.trace(
        "[CAPTCHA-CORE] Injected instances in CaptchaServiceFactory is {}", services.keySet());
    CaptchaServiceFactory.instance.setServices(services);
    return CaptchaServiceFactory.instance;
  }
}
