package tutorials4j.framework.common.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.validation.LocalDateTimeValidator;

/**
 * 校验器配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ValidatorsConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[COMMON-CORE] Validators Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    LocalDateTimeValidator localDateTimeValidator() {
        log.debug("[COMMON-CORE] Local DateTime Validator");
        return new LocalDateTimeValidator();
    }
}
