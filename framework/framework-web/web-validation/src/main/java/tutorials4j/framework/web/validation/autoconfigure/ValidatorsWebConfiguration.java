package tutorials4j.framework.web.validation.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.validation.LocalDateTimeValidator;

/**
 * Web 校验器自动配置：注册 Web 校验相关的校验器 Bean（容器中不存在时生效）。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ValidatorsWebConfiguration {

  /** 初始化日志输出 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-VALIDATION] Validation Web Configuration");
  }

  /**
   * 注册 {@link LocalDateTimeValidator} Bean（容器中不存在时生效），用于支持 {@code @LocalDateTimeFormat}
   * 注解的日期时间格式校验。
   *
   * @return 日期时间格式校验器
   */
  @Bean
  @ConditionalOnMissingBean
  LocalDateTimeValidator localDateTimeValidator() {
    log.trace("[WEB-VALIDATION] Local DateTime Validator");
    return new LocalDateTimeValidator();
  }
}
