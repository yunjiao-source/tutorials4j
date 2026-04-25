package tutorials4j.framework.web.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.properties.WebClientProperties;
import tutorials4j.framework.web.core.properties.WebHttpProperties;

/**
 * web core 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({WebHttpProperties.class, WebClientProperties.class})
public class WebCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Web Core Configuration");
    }

}
