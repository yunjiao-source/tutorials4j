package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * web http 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({WebMvcCachedBodyConfiguration.class, WebMvcSecurityConfiguration.class
        ,WebMvcRequestLoggingConfiguration.class})
public class WebMvcConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Web Mvc Configuration");
    }

}
