package tutorials4j.framework.feature.totp.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.feature.totp.web.TotpAuthEndpoint;
import tutorials4j.framework.web.security.totp.GoogleAuthService;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE, name = "totp-enabled")
public class TotpFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[FEATURE-TOTP] TOTP Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  TotpAuthEndpoint totpAuthEndpoint(GoogleAuthService googleAuthService) {
    log.debug("[FEATURE-TOTP] Totp Auth Endpoint");
    return new TotpAuthEndpoint(googleAuthService);
  }
}
