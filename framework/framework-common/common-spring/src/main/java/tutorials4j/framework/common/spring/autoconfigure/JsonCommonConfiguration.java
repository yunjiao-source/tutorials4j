package tutorials4j.framework.common.spring.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.spring.jackson.CommonSimpleModule;
import tutorials4j.framework.common.spring.jackson.JacksonUtils;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;

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
    log.trace("[COMMON-SPRING] Json Common Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  JacksonUtils jackson2Utils(ObjectMapper objectMapper) {
    log.trace("[COMMON-SPRING] Jackson2 Utils");
    JacksonUtils.instance.setObjectMapper(objectMapper);
    return JacksonUtils.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  CommonSimpleModule commonSimpleModule() {
    log.trace("[COMMON-SPRING] Common Simple Module");
    return new CommonSimpleModule();
  }

  @Bean
  @ConditionalOnMissingBean
  ObjectMapperCreator objectMapperCreator(ObjectMapper objectMapper) {
    log.trace("[COMMON-SPRING] Object Mapper Creator");
    return new ObjectMapperCreator(objectMapper);
  }
}
