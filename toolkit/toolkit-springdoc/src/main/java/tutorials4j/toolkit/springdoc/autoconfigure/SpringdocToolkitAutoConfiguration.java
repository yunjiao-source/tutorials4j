package tutorials4j.toolkit.springdoc.autoconfigure;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
public class SpringdocToolkitAutoConfiguration implements WebMvcConfigurer {
  @Value("${spring.application.name:}")
  private String name;

  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-SPRINGDOC] Springdoc Toolkit Auto Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  OpenAPI toolkitOpenAPI() {
    log.trace("[TOOLKIT-SPRINGDOC] Toolkit Open API");
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
}
