package tutorials4j.framework.web.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.core.properties.WebProperties;

/**
 * web core 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({WebHttpProperties.class, WebProperties.class})
public class WebCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Web Core Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    AccessLimitedCacheTemplate accessLimitedCacheTemplate() {
        log.debug("Tutorials4j - Web |- Access Limited Cache Template");
        return new AccessLimitedCacheTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    IdempotentCacheTemplate idempotentCacheTemplate() {
        log.debug("Tutorials4j - Web |- Idempotent Cache Template");
        return new IdempotentCacheTemplate();
    }
}
