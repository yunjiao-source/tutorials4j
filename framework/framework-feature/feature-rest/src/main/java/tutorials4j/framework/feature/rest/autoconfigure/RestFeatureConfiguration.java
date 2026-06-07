package tutorials4j.framework.feature.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

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
}
