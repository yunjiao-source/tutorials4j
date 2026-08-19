package tutorials4j.framework.captcha.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.captcha.hutool.autoconfigure.HutoolCaptchaConfiguration;
import tutorials4j.framework.captcha.tianai.autoconfigure.TianaiCaptchaConfiguration;
import tutorials4j.framework.captcha.web.autoconfigure.WebCaptchaConfiguration;

/**
 * 验证码模块自动配置入口类。
 *
 * <p>汇总导入验证码核心、Hutool、天意及 Web 各子模块的自动配置，供 Spring Boot 自动装配加载。
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  CaptchaConfiguration.class,
  HutoolCaptchaConfiguration.class,
  TianaiCaptchaConfiguration.class,
  WebCaptchaConfiguration.class
})
public class CaptchaAutoConfiguration {
  /** 初始化：输出验证码模块自动配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[COMMON] Common Auto Configuration");
  }
}
