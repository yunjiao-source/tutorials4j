package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.rest.autoconfigure.WebRestConfiguration;
import tutorials4j.framework.web.core.autoconfigure.WebCoreConfiguration;
import tutorials4j.framework.web.http.autoconfigure.WebHttpConfiguration;

/**
 * 缓存请求体自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({WebCoreConfiguration.class,
        WebRestConfiguration.class,
        WebHttpConfiguration.class})
public class WebAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Web Auto Configuration");
    }

}
