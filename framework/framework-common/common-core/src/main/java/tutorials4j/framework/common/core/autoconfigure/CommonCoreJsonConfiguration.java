package tutorials4j.framework.common.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.json.BaseEnumSimpleModule;
import tutorials4j.framework.common.core.json.LongJsSimpleModule;

/**
 * 公共核心Json模块的配置类
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CommonCoreJsonConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[COMMON-CORE] Common Core Json Configuration");
    }


    @Bean
    BaseEnumSimpleModule baseEnumSimpleModule() {
        log.debug("[COMMON-CORE] Base Enum Simple Module");
        return new BaseEnumSimpleModule();
    }

    @Bean
    LongJsSimpleModule longJsSimpleModule() {
        log.debug("[COMMON-CORE] Long Js Simple Module");
        return new LongJsSimpleModule();
    }

}
