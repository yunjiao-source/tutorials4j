package tutorials4j.framework.data.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

/**
 * Data核心配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DataTenantProperties.class)
public class DataCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Data |- Data Core Configuration");
    }

}
