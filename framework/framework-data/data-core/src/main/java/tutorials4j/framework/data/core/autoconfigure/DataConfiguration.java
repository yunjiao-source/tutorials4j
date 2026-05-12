package tutorials4j.framework.data.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.core.properties.MybatisPlusProperties;
import tutorials4j.framework.data.core.properties.DataProperties;

/**
 * Data核心配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({DataProperties.class, MybatisPlusProperties.class})
public class DataConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[DATA-CORE] Data Core Configuration");
    }

}
