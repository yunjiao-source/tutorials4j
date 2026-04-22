package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CachesProperties;

/**
 * 缓存核心配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CachesProperties.class)
public class CacheCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Cache Core Configuration");
    }

}
