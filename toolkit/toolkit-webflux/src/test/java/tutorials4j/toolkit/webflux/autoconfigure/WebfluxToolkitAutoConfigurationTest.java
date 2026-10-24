package tutorials4j.toolkit.webflux.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.micrometer.tracing.Tracer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import tutorials4j.toolkit.webflux.component.GlobalWebfluxExceptionHandler;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class WebfluxToolkitAutoConfigurationTest {
  private static final String TOOLKIT_PACKAGE = "tutorials4j.toolkit.webflux";

  private final ReactiveWebApplicationContextRunner reactiveRunner =
      new ReactiveWebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(WebfluxToolkitAutoConfiguration.class))
          .withBean(Tracer.class, () -> mock(Tracer.class));

  private final WebApplicationContextRunner servletRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(WebfluxToolkitAutoConfiguration.class))
          .withBean(Tracer.class, () -> mock(Tracer.class));

  @Test
  @DisplayName("Reactive Web 应用下应装配 GlobalWebfluxExceptionHandler")
  void shouldRegisterHandlerInReactiveWebApp() {
    reactiveRunner.run(
        context -> {
          assertThat(context).hasSingleBean(GlobalWebfluxExceptionHandler.class);
          assertThat(context).hasSingleBean(WebfluxToolkitAutoConfiguration.class);
        });
  }

  @Test
  @DisplayName("Servlet Web 应用下不应装配任何 Bean")
  void shouldNotRegisterInServletWebApp() {
    servletRunner.run(
        context -> {
          assertThat(context).doesNotHaveBean(GlobalWebfluxExceptionHandler.class);
          assertThat(context).doesNotHaveBean(WebfluxToolkitAutoConfiguration.class);
        });
  }

  @Test
  @DisplayName("非 Web 应用下不应装配任何 Bean")
  void shouldNotRegisterInNonWebApp() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(WebfluxToolkitAutoConfiguration.class))
        .withBean(Tracer.class, () -> mock(Tracer.class))
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(GlobalWebfluxExceptionHandler.class);
            });
  }

  @Test
  @DisplayName("用户自定义 Bean 时应允许覆盖默认装配")
  void shouldBackOffWhenUserDefinesBean() {
    reactiveRunner
        .withUserConfiguration(CustomConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(GlobalWebfluxExceptionHandler.class);
              assertThat(context.getBean(GlobalWebfluxExceptionHandler.class))
                  .isSameAs(CustomConfig.CUSTOM);
            });
  }

  @Test
  @DisplayName("按真实自动配置清单加载：Servlet Web 应用下不应装配任何 Bean")
  void shouldNotRegisterInServletWebAppLoadedFromImports() {
    new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(toolkitAutoConfigurationCandidates()))
        .withBean(Tracer.class, () -> mock(Tracer.class))
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(GlobalWebfluxExceptionHandler.class);
              assertThat(context).doesNotHaveBean(WebfluxToolkitAutoConfiguration.class);
            });
  }

  @Test
  @DisplayName("按真实自动配置清单加载：Reactive Web 应用下应装配处理器")
  void shouldRegisterInReactiveWebAppLoadedFromImports() {
    new ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(toolkitAutoConfigurationCandidates()))
        .withBean(Tracer.class, () -> mock(Tracer.class))
        .run(
            context -> {
              assertThat(context).hasSingleBean(WebfluxToolkitAutoConfiguration.class);
              assertThat(context).hasSingleBean(GlobalWebfluxExceptionHandler.class);
            });
  }

  @Test
  @DisplayName("META-INF 自动配置清单登记的是 WebfluxToolkitAutoConfiguration 本身")
  void shouldRegisterAutoConfigurationClassInImports() {
    assertThat(toolkitAutoConfigurationCandidates())
        .containsExactly(WebfluxToolkitAutoConfiguration.class);
  }

  /**
   * 从 classpath 上真实的 {@code AutoConfiguration.imports} 清单解析本模块的自动配置类。
   *
   * @return 本模块登记的自动配置类
   */
  private static Class<?>[] toolkitAutoConfigurationCandidates() {
    List<Class<?>> candidates = new ArrayList<>();
    ClassLoader classLoader = WebfluxToolkitAutoConfigurationTest.class.getClassLoader();
    ImportCandidates.load(AutoConfiguration.class, classLoader)
        .forEach(
            className -> {
              if (className.startsWith(TOOLKIT_PACKAGE)) {
                candidates.add(ClassUtils.resolveClassName(className, classLoader));
              }
            });
    return candidates.toArray(new Class<?>[0]);
  }

  @Configuration(proxyBeanMethods = false)
  static class CustomConfig {
    static final GlobalWebfluxExceptionHandler CUSTOM =
        new GlobalWebfluxExceptionHandler(mock(Tracer.class));

    @Bean
    GlobalWebfluxExceptionHandler customHandler() {
      return CUSTOM;
    }
  }
}
