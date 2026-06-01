package tutorials4j.framework.web.validation.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.validation.LocalDateTimeValidator;

/**
 * 校验器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ValidatorsWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-VALIDATION] Web Validation Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  LocalDateTimeValidator localDateTimeValidator() {
    log.debug("[WEB-VALIDATION] Local DateTime Validator");
    return new LocalDateTimeValidator();
  }
}
