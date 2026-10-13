package tutorials4j.toolkit.json.autoconfigure;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.Ordered;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tutorials4j.toolkit.json.component.JsonTypeWhiteListLoader;
import tutorials4j.toolkit.json.component.ObjectMapperCreator;
import tutorials4j.toolkit.json.component.ToolkitSimpleModule;
import tutorials4j.toolkit.json.util.JacksonUtils;

/**
 * {@link JsonToolkitAutoConfiguration} 自动装配测试。
 *
 * @author YunJiao Yang
 */
class JsonToolkitAutoConfigurationTest {
  private static final String AUTO_CONFIGURATION_IMPORTS =
      "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(JsonToolkitAutoConfiguration.class))
          .withBean(ObjectMapper.class, ObjectMapper::new);

  @Test
  @DisplayName("装配 SimpleModule、ObjectMapperCreator、JacksonUtils 与白名单加载器")
  void shouldRegisterBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(JsonToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(ToolkitSimpleModule.class);
          assertThat(context).hasSingleBean(ObjectMapperCreator.class);
          assertThat(context).hasSingleBean(JacksonUtils.class);
          assertThat(context).hasSingleBean(JsonTypeWhiteListLoader.class);
        });
  }

  @Test
  @DisplayName("启动后将 ObjectMapper 注入 JacksonUtils 静态单例")
  void shouldInitializeJacksonUtilsSingleton() {
    contextRunner.run(
        context -> {
          assertThat(context.getBean(JacksonUtils.class)).isSameAs(JacksonUtils.instance);
          assertThat(JacksonUtils.instance.toJson(Map.of("name", "alice"))).contains("\"alice\"");
        });
  }

  @Test
  @DisplayName("ObjectMapperCreator 拷贝容器中的 ObjectMapper（含已注册模块）")
  void shouldWireProvidedObjectMapperIntoCreator() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(JsonToolkitAutoConfiguration.class))
        .withBean(
            ObjectMapper.class,
            () -> JsonMapper.builder().addModule(new ToolkitSimpleModule()).build())
        .run(
            context -> {
              ObjectMapperCreator creator = context.getBean(ObjectMapperCreator.class);

              assertThat(creator.getBeanClass()).isEqualTo(ObjectMapper.class);
              assertThat(creator.getInstance().writeValueAsString(1L)).isEqualTo("\"1\"");
              assertThat(creator.newInstance().writeValueAsString(1L)).isEqualTo("\"1\"");
            });
  }

  @Test
  @DisplayName("白名单加载器在单例初始化完成后自动加载清单")
  void shouldLoadWhiteListAfterSingletonsInstantiated() {
    contextRunner.run(
        context -> {
          JsonTypeWhiteListLoader loader = context.getBean(JsonTypeWhiteListLoader.class);

          assertThat(loader.contains("java.util.ArrayList")).isTrue();
          assertThat(loader.contains("java.util.LinkedHashMap")).isTrue();
          assertThat(loader.contains("com.example.NotListed")).isFalse();
        });
  }

  @Test
  @DisplayName("ToolkitSimpleModule 以 100 的顺序注册为 Ordered Bean")
  void shouldRegisterOrderedModule() {
    contextRunner.run(
        context -> {
          ToolkitSimpleModule module = context.getBean(ToolkitSimpleModule.class);

          assertThat(module).isInstanceOf(Ordered.class);
          assertThat(((Ordered) module).getOrder()).isEqualTo(100);
        });
  }

  @Test
  @DisplayName("类标注 @AutoConfiguration")
  void shouldBeAnnotatedAsAutoConfiguration() {
    assertThat(JsonToolkitAutoConfiguration.class.getAnnotation(AutoConfiguration.class))
        .isNotNull();
  }

  @Test
  @DisplayName("已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() throws IOException {
    try (InputStream stream =
        getClass().getClassLoader().getResourceAsStream(AUTO_CONFIGURATION_IMPORTS)) {
      assertThat(stream).isNotNull();
      assertThat(new String(stream.readAllBytes(), UTF_8))
          .contains(JsonToolkitAutoConfiguration.class.getName());
    }
  }
}
