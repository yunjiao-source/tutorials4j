package tutorials4j.framework.captcha.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.captcha.hutool.autoconfigure.HutoolCaptchaConfiguration;
import tutorials4j.framework.captcha.tianai.autoconfigure.TianaiCaptchaConfiguration;

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
  TianaiCaptchaConfiguration.class
})
public class CaptchaAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[COMMON] Common Auto Configuration");
  }
}
