package tutorials4j.toolkit.webmvc.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.micrometer.tracing.Tracer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import tutorials4j.toolkit.webmvc.GlobalWebmvcExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WebmvcToolkitAutoConfigurationTest {
  private static final String TOOLKIT_PACKAGE = "tutorials4j.toolkit.webmvc";

  private final WebApplicationContextRunner servletRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(WebmvcToolkitAutoConfiguration.class));

  private final ApplicationContextRunner nonWebRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(WebmvcToolkitAutoConfiguration.class));

  @Test
  @DisplayName("当容器中存在 Tracer 时，应自动注册 GlobalWebmvcExceptionHandler")
  void shouldRegisterGlobalWebmvcExceptionHandlerWhenTracerPresent() {
    servletRunner
        .withUserConfiguration(TracerConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(GlobalWebmvcExceptionHandler.class);
              assertThat(context.getBean(GlobalWebmvcExceptionHandler.class)).isNotNull();
            });
  }

  @Test
  @DisplayName("当容器中不存在 Tracer 时，不应注册 GlobalWebmvcExceptionHandler")
  void shouldNotRegisterGlobalWebmvcExceptionHandlerWhenTracerAbsent() {
    servletRunner.run(
        context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .isInstanceOf(UnsatisfiedDependencyException.class);
        });
  }

  @Test
  @DisplayName("当用户已自定义 GlobalWebmvcExceptionHandler 时，应保留用户 Bean")
  void shouldBackOffWhenUserDefinesGlobalWebmvcExceptionHandler() {
    servletRunner
        .withUserConfiguration(TracerConfiguration.class, CustomHandlerConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(GlobalWebmvcExceptionHandler.class);
              assertThat(context.getBean(GlobalWebmvcExceptionHandler.class))
                  .isSameAs(CustomHandlerConfiguration.CUSTOM_HANDLER);
            });
  }

  @Test
  @DisplayName("META-INF 自动配置清单登记的是 WebmvcToolkitAutoConfiguration 本身")
  void shouldRegisterAutoConfigurationClassInImports() {
    assertThat(toolkitAutoConfigurationCandidates())
        .containsExactly(WebmvcToolkitAutoConfiguration.class);
  }

  @Test
  @DisplayName("按真实自动配置清单加载：Servlet Web 应用下装配处理器，Bean 名称稳定")
  void shouldRegisterHandlerLoadedFromImports() {
    new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(toolkitAutoConfigurationCandidates()))
        .withBean(Tracer.class, () -> mock(Tracer.class))
        .run(
            context -> {
              assertThat(context).hasBean("globalWebmvcExceptionHandler");
              assertThat(context).hasSingleBean(WebmvcToolkitAutoConfiguration.class);
              assertThat(context).hasSingleBean(GlobalWebmvcExceptionHandler.class);
            });
  }

  @Test
  @DisplayName("处理器使用容器中的 Tracer 实例")
  void shouldWireTracerIntoHandler() {
    Tracer tracer = mock(Tracer.class);

    servletRunner
        .withBean(Tracer.class, () -> tracer)
        .run(
            context ->
                assertThat(context.getBean(GlobalWebmvcExceptionHandler.class).getTrace())
                    .isSameAs(tracer));
  }

  @Test
  @DisplayName("按真实自动配置清单加载：Reactive Web 应用下不应装配任何 Bean")
  void shouldNotRegisterInReactiveWebApp() {
    new ReactiveWebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(toolkitAutoConfigurationCandidates()))
        .withBean(Tracer.class, () -> mock(Tracer.class))
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(WebmvcToolkitAutoConfiguration.class);
              assertThat(context).doesNotHaveBean(GlobalWebmvcExceptionHandler.class);
            });
  }

  @Test
  @DisplayName("非 Web 应用下不应装配任何 Bean")
  void shouldNotRegisterInNonWebApp() {
    nonWebRunner
        .withBean(Tracer.class, () -> mock(Tracer.class))
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(WebmvcToolkitAutoConfiguration.class);
              assertThat(context).doesNotHaveBean(GlobalWebmvcExceptionHandler.class);
            });
  }

  /**
   * 从 classpath 上真实的 {@code AutoConfiguration.imports} 清单解析本模块的自动配置类。
   *
   * @return 本模块登记的自动配置类
   */
  private static Class<?>[] toolkitAutoConfigurationCandidates() {
    List<Class<?>> candidates = new ArrayList<>();
    ClassLoader classLoader = WebmvcToolkitAutoConfigurationTest.class.getClassLoader();
    ImportCandidates.load(AutoConfiguration.class, classLoader)
        .forEach(
            className -> {
              if (className.startsWith(TOOLKIT_PACKAGE)) {
                candidates.add(ClassUtils.resolveClassName(className, classLoader));
              }
            });
    return candidates.toArray(new Class<?>[0]);
  }

  // ---------------------------------------------------------------------
  // 测试用配置
  // ---------------------------------------------------------------------

  @Configuration(proxyBeanMethods = false)
  static class TracerConfiguration {

    @Bean
    Tracer tracer() {
      return mock(Tracer.class);
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class CustomHandlerConfiguration {

    static final GlobalWebmvcExceptionHandler CUSTOM_HANDLER =
        new GlobalWebmvcExceptionHandler(mock(Tracer.class));

    @Bean
    GlobalWebmvcExceptionHandler customGlobalWebmvcExceptionHandler() {
      return CUSTOM_HANDLER;
    }
  }
}
