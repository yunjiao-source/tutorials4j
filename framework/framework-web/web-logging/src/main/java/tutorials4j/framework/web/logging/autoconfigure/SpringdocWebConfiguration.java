package tutorials4j.framework.web.logging.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.web.logging.SimpleSwaggerModelResolver;

/**
 * todo
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({SpringDocConfiguration.class})
public class SpringdocWebConfiguration implements WebMvcConfigurer {
  @Value("${spring.application.name:}")
  private String name;

  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-LOGGING] Web Springdoc Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  OpenAPI t4jOpenAPI() {
    log.debug("[WEB-LOGGING] T4J Open API");
    return new OpenAPI()
        .info(
            new Info()
                .title("接口文档")
                .version("Swagger V3")
                .description(name + "的应用服务文档")
                .license(new License().name("MIT License").url("https://mit-license.org/")))
        .externalDocs(
            new ExternalDocumentation()
                .description("Tutorials For Java")
                .url("https://gitee.com/yunjiao-source/tutorials4j"));
  }

  @Bean
  @ConditionalOnBean(ObjectMapper.class)
  @ConditionalOnMissingBean
  ModelResolver simpleSwaggerModelResolver(ObjectMapper mapper) {
    log.debug("[WEB-LOGGING] Simple Validation Model Resolver");
    return new SimpleSwaggerModelResolver(mapper);
  }
}
