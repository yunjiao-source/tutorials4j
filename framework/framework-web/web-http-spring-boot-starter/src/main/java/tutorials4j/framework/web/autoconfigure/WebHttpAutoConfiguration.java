package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.http.CachedRequestBodyConfiguration;

/**
 * 缓存请求体自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CachedRequestBodyConfiguration.class})
public class WebHttpAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Cached Request Body Auto Configuration");
    }

}
