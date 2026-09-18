package tutorials4j.toolkit.json.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;
import tutorials4j.toolkit.json.ObjectMapperCreator;
import tutorials4j.toolkit.json.module.ToolkitSimpleModule;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class JsonToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-JSON] Json Toolkit Auto Configuration");
  }

  @Bean
  ToolkitSimpleModule toolkitSimpleModule() {
    log.trace("[TOOLKIT-JSON] Toolkit Simple Module");
    return new ToolkitSimpleModule();
  }

  @Bean
  ObjectMapperCreator objectMapperCreator(ObjectMapper objectMapper) {
    log.trace("[TOOLKIT-JSON] Object Mapper Creator");
    return new ObjectMapperCreator(objectMapper);
  }

  @Bean
  JacksonUtils jackson2Utils(ObjectMapperCreator objectMapperCreator) {
    log.trace("[TOOLKIT-JSON] Jackson2 Utils");
    JacksonUtils.instance.setObjectMapper(objectMapperCreator.getInstance());
    return JacksonUtils.instance;
  }
}
