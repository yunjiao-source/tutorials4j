package tutorials4j.framework.common.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.common.core.autoconfigure.CommonCoreConfiguration;
import tutorials4j.framework.common.core.autoconfigure.CommonCoreJsonConfiguration;

/**
 * 通用模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({CommonCoreConfiguration.class, CommonCoreJsonConfiguration.class})
public class CommonAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Common |- Common Auto Configuration");
    }

}
