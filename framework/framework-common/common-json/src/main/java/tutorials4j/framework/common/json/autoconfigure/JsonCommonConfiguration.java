package tutorials4j.framework.common.json.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.json.BaseEnumSimpleModule;
import tutorials4j.framework.common.json.Long2StringSimpleModule;
import tutorials4j.framework.common.json.util.Jackson2Utils;

/**
 * 公共核心Json模块的配置类
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class JsonCommonConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[COMMON-JSON] Common Json Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  Jackson2Utils jackson2Utils(ObjectMapper objectMapper) {
    log.debug("[COMMON-JSON] Jackson2 Utils");
    Jackson2Utils.instance.setObjectMapper(objectMapper);
    return Jackson2Utils.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  BaseEnumSimpleModule baseEnumSimpleModule() {
    log.debug("[COMMON-JSON] Base Enum Simple Module");
    return new BaseEnumSimpleModule();
  }

  @Bean
  @ConditionalOnMissingBean
  Long2StringSimpleModule longJsSimpleModule() {
    log.debug("[COMMON-JSON] Long Js Simple Module");
    return new Long2StringSimpleModule();
  }
}
