package tutorials4j.toolkit.webflux.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.webflux.GlobalWebfluxExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WebfluxToolkitAutoConfigurationTest {
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
