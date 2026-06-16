package tutorials4j.framework.feature.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.feature.captcha.autoconfigure.CaptchaFeatureConfiguration;
import tutorials4j.framework.feature.crypto.autoconfigure.CryptoFeatureConfiguration;
import tutorials4j.framework.feature.signin.autoconfigure.SignInFeatureConfiguration;
import tutorials4j.framework.feature.totp.autoconfigure.TotpFeatureConfiguration;

/**
 * 功能模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({
  FeatureConfiguration.class,
  SignInFeatureConfiguration.class,
  CaptchaFeatureConfiguration.class,
  CryptoFeatureConfiguration.class,
  TotpFeatureConfiguration.class
})
public class FeatureAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE] Feature Auto Configuration");
  }
}
