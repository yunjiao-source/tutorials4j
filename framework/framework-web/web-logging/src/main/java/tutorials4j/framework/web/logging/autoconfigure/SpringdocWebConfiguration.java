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
 * Springdoc（OpenAPI 文档）相关组件的自动配置类。
 *
 * <p>在 SpringDoc 配置 Bean 存在时生效，注册默认的 {@link OpenAPI} 文档信息 以及自定义的 {@link SimpleSwaggerModelResolver}
 * 模型解析器。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({SpringDocConfiguration.class})
public class SpringdocWebConfiguration implements WebMvcConfigurer {
  /** 应用名称，用于生成接口文档描述（来自 {@code spring.application.name} 配置）。 */
  @Value("${spring.application.name:}")
  private String name;

  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-LOGGING] Web Springdoc Configuration");
  }

  /**
   * 注册默认的 OpenAPI 文档信息 Bean（标题、版本、描述、许可证等）。
   *
   * @return OpenAPI 文档对象
   */
  @Bean
  @ConditionalOnMissingBean
  OpenAPI t4jOpenAPI() {
    log.trace("[WEB-LOGGING] T4J Open API");
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

  /**
   * 注册自定义的 Swagger 模型解析器 Bean（存在 {@link ObjectMapper} 时生效）。
   *
   * @param mapper Jackson 对象映射器
   * @return 自定义模型解析器实例
   */
  @Bean
  @ConditionalOnBean(ObjectMapper.class)
  @ConditionalOnMissingBean
  ModelResolver simpleSwaggerModelResolver(ObjectMapper mapper) {
    log.trace("[WEB-LOGGING] Simple Validation Model Resolver");
    return new SimpleSwaggerModelResolver(mapper);
  }
}
