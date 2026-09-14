package tutorials4j.toolkit.webmvc.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.webmvc.GlobalWebmvcExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class WebToolkitAutoConfigurationTest {
  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(WebToolkitAutoConfiguration.class)
          .withUserConfiguration(TracerTestConfig.class);

  @Test
  void shouldRegisterAllBeans() {
    contextRunner.run(
        context -> {
          assertThat(context.getBean(GlobalWebmvcExceptionHandler.class)).isNotNull();
        });
  }

  @Configuration(proxyBeanMethods = false)
  static class TracerTestConfig {
    @Bean
    Tracer tracer() {
      return mock(Tracer.class);
    }
  }
}
