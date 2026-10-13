package tutorials4j.toolkit.springdoc.autoconfigure;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@link SpringdocToolkitAutoConfiguration} 自动装配测试。
 *
 * @author YunJiao Yang
 */
class SpringdocToolkitAutoConfigurationTest {
  private static final String AUTO_CONFIGURATION_IMPORTS =
      "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SpringdocToolkitAutoConfiguration.class));

  @Configuration(proxyBeanMethods = false)
  static class CustomOpenApiConfig {
    static final OpenAPI CUSTOM = new OpenAPI().info(new Info());

    @Bean
    OpenAPI customOpenApi() {
      return CUSTOM;
    }
  }

  @Test
  @DisplayName("装配名为 toolkitOpenAPI 的 OpenAPI Bean")
  void shouldRegisterOpenApiBean() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(SpringdocToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(OpenAPI.class);
          assertThat(context).hasBean("toolkitOpenAPI");
          assertThat(context.getBean(OpenAPI.class)).isNotNull();
        });
  }

  @Test
  @DisplayName("OpenAPI 静态信息：标题、版本与许可证")
  void shouldBuildApiInfo() {
    contextRunner.run(
        context -> {
          OpenAPI openApi = context.getBean(OpenAPI.class);

          assertThat(openApi.getInfo().getTitle()).isEqualTo("接口文档");
          assertThat(openApi.getInfo().getVersion()).isEqualTo("Swagger V3");
          assertThat(openApi.getInfo().getLicense().getName()).isEqualTo("MIT License");
          assertThat(openApi.getInfo().getLicense().getUrl()).isEqualTo("https://mit-license.org/");
        });
  }

  @Test
  @DisplayName("OpenAPI 外部文档指向 Tutorials For Java")
  void shouldBuildExternalDocumentation() {
    contextRunner.run(
        context -> {
          OpenAPI openApi = context.getBean(OpenAPI.class);

          assertThat(openApi.getExternalDocs()).isNotNull();
          assertThat(openApi.getExternalDocs().getDescription()).isEqualTo("Tutorials For Java");
          assertThat(openApi.getExternalDocs().getUrl())
              .isEqualTo("https://gitee.com/yunjiao-source/tutorials4j");
        });
  }

  @Test
  @DisplayName("描述中拼接 spring.application.name")
  void shouldUseApplicationNameInDescription() {
    contextRunner
        .withPropertyValues("spring.application.name=tutorials4j-demo")
        .run(
            context -> {
              OpenAPI openApi = context.getBean(OpenAPI.class);

              assertThat(openApi.getInfo().getDescription()).isEqualTo("tutorials4j-demo的应用服务文档");
            });
  }

  @Test
  @DisplayName("未配置 spring.application.name 时描述使用空名称")
  void shouldUseEmptyApplicationNameByDefault() {
    contextRunner.run(
        context -> {
          OpenAPI openApi = context.getBean(OpenAPI.class);

          assertThat(openApi.getInfo().getDescription()).isEqualTo("的应用服务文档");
        });
  }

  @Test
  @DisplayName("用户自定义 OpenAPI Bean 时自动装配退让")
  void shouldBackOffWhenUserDefinesOpenApi() {
    contextRunner
        .withUserConfiguration(CustomOpenApiConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(OpenAPI.class);
              assertThat(context.getBean(OpenAPI.class)).isSameAs(CustomOpenApiConfig.CUSTOM);
            });
  }

  @Test
  @DisplayName("Servlet Web 环境下同样装配 OpenAPI Bean")
  void shouldRegisterOpenApiBeanInServletWebContext() {
    new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringdocToolkitAutoConfiguration.class))
        .withPropertyValues("spring.application.name=tutorials4j-web")
        .run(
            context -> {
              assertThat(context).hasSingleBean(OpenAPI.class);
              assertThat(context.getBean(OpenAPI.class).getInfo().getDescription())
                  .isEqualTo("tutorials4j-web的应用服务文档");
            });
  }

  @Test
  @DisplayName("类标注 @AutoConfiguration 且实现 WebMvcConfigurer")
  void shouldBeAutoConfigurationAndWebMvcConfigurer() {
    assertThat(SpringdocToolkitAutoConfiguration.class.getAnnotation(AutoConfiguration.class))
        .isNotNull();
    assertThat(WebMvcConfigurer.class).isAssignableFrom(SpringdocToolkitAutoConfiguration.class);
  }

  @Test
  @DisplayName("已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() throws IOException {
    try (InputStream stream =
        getClass().getClassLoader().getResourceAsStream(AUTO_CONFIGURATION_IMPORTS)) {
      assertThat(stream).isNotNull();
      assertThat(new String(stream.readAllBytes(), UTF_8))
          .contains(SpringdocToolkitAutoConfiguration.class.getName());
    }
  }
}
