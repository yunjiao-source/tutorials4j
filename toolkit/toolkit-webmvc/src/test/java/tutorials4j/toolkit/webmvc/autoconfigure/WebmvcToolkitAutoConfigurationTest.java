package tutorials4j.toolkit.webmvc.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.webmvc.GlobalWebmvcExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WebmvcToolkitAutoConfigurationTest {
  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(WebmvcToolkitAutoConfiguration.class));

  @Test
  @DisplayName("当容器中存在 Tracer 时，应自动注册 GlobalWebmvcExceptionHandler")
  void shouldRegisterGlobalWebmvcExceptionHandlerWhenTracerPresent() {
    contextRunner
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
    contextRunner.run(
        context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .isInstanceOf(UnsatisfiedDependencyException.class);
        });
  }

  @Test
  @DisplayName("当用户已自定义 GlobalWebmvcExceptionHandler 时，应保留用户 Bean")
  void shouldBackOffWhenUserDefinesGlobalWebmvcExceptionHandler() {
    contextRunner
        .withUserConfiguration(TracerConfiguration.class, CustomHandlerConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(GlobalWebmvcExceptionHandler.class);
              assertThat(context.getBean(GlobalWebmvcExceptionHandler.class))
                  .isSameAs(CustomHandlerConfiguration.CUSTOM_HANDLER);
            });
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
