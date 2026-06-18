package tutorials4j.framework.captcha.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.captcha.hutool.autoconfigure.HutoolCaptchaConfiguration;
import tutorials4j.framework.captcha.tianai.autoconfigure.TianaiCaptchaConfiguration;
import tutorials4j.framework.captcha.web.autoconfigure.WebCaptchaConfiguration;

/**
 * 验证码自动配置
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
  @PostConstruct
  public void postConstruct() {
    log.trace("[COMMON] Common Auto Configuration");
  }
}
