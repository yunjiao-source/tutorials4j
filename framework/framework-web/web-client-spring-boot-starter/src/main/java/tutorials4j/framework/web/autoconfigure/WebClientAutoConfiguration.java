package tutorials4j.framework.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.client.WebClientConfiguration;

/**
 * http5 自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration(after = {RestTemplateAutoConfiguration.class})
@Import({WebClientConfiguration.class})
public class WebClientAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Web Client Auto Configuration");
    }

}
