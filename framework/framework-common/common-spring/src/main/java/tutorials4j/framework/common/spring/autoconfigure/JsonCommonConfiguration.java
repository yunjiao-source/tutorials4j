package tutorials4j.framework.common.spring.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.spring.jackson.CommonSimpleModule;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.jackson.JacksonUtils;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;

/**
 * 公共核心 Json 模块的自动配置类。
 *
 * <p>装配 Jackson 相关的基础组件：{@link JacksonUtils} 工具单例、{@link CommonSimpleModule} 公共序列化模块、 {@link
 * ObjectMapperCreator} 以及 {@link JacksonRecord} 支持。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class JsonCommonConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[COMMON-SPRING] Json Common Configuration");
  }

  /**
   * 创建 Jackson 工具类单例并注入 ObjectMapper。
   *
   * @param objectMapper Jackson 对象映射器
   * @return {@link JacksonUtils} 单例实例
   */
  @Bean
  @ConditionalOnMissingBean
  JacksonUtils jackson2Utils(ObjectMapper objectMapper) {
    log.trace("[COMMON-SPRING] Jackson2 Utils");
    JacksonUtils.instance.setObjectMapper(objectMapper);
    return JacksonUtils.instance;
  }

  /**
   * 创建公共的 Jackson 序列化模块。
   *
   * @return {@link CommonSimpleModule} 实例
   */
  @Bean
  @ConditionalOnMissingBean
  CommonSimpleModule commonSimpleModule() {
    log.trace("[COMMON-SPRING] Common Simple Module");
    return new CommonSimpleModule();
  }

  /**
   * 创建 ObjectMapper 创建器。
   *
   * @param objectMapper Jackson 对象映射器
   * @return {@link ObjectMapperCreator} 实例
   */
  @Bean
  @ConditionalOnMissingBean
  ObjectMapperCreator objectMapperCreator(ObjectMapper objectMapper) {
    log.trace("[COMMON-SPRING] Object Mapper Creator");
    return new ObjectMapperCreator(objectMapper);
  }

  /**
   * 创建 Jackson Record 支持组件。
   *
   * @param objectMapperCreator ObjectMapper 创建器
   * @return {@link JacksonRecord} 实例
   */
  @Bean
  @ConditionalOnMissingBean
  JacksonRecord jacksonRecord(ObjectMapperCreator objectMapperCreator) {
    log.trace("[COMMON-SPRING] Jackson Record");
    return new JacksonRecord(objectMapperCreator.getInstance());
  }
}
