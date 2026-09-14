package tutorials4j.toolkit.core.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ToolkitPropertiesTest {
  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(ToolkitProperties.class)
  static class TestConfig {}

  @Test
  void shouldUseDefaultValues() {
    contextRunner.run(
        context -> {
          ToolkitProperties properties = context.getBean(ToolkitProperties.class);
          assertThat(properties.getScheduledThreadPoolExecutor()).isNotNull();
          assertThat(properties.getThreadPoolExecutor()).isNotNull();
        });
  }

  @Test
  void shouldBindScheduledThreadPoolExecutorProperties() {
    String prefix = PropertyConsts.PROPERTY_TOOLKIT + ".scheduled-thread-pool-executor";
    contextRunner
        .withPropertyValues(
            prefix + ".daemon=true",
            prefix + ".core-pool-size=2",
            prefix + ".thread-name-prefix=scheduled-",
            prefix + ".await-termination=true",
            prefix + ".await-termination-period=30s")
        .run(
            context -> {
              ToolkitProperties properties = context.getBean(ToolkitProperties.class);
              ToolkitProperties.ScheduledExecutionOptions options =
                  properties.getScheduledThreadPoolExecutor();

              assertThat(options.getDaemon()).isTrue();
              assertThat(options.getCorePoolSize()).isEqualTo(2);
              assertThat(options.getThreadNamePrefix()).isEqualTo("scheduled-");
              assertThat(options.getAwaitTermination()).isTrue();
              assertThat(options.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(30));
            });
  }

  @Test
  void shouldBindThreadPoolExecutorProperties() {
    String prefix = PropertyConsts.PROPERTY_TOOLKIT + ".thread-pool-executor";
    contextRunner
        .withPropertyValues(
            prefix + ".daemon=false",
            prefix + ".core-pool-size=3",
            prefix + ".thread-name-prefix=thread-",
            prefix + ".await-termination=false",
            prefix + ".await-termination-period=10s",
            prefix + ".maximum-pool-size=10",
            prefix + ".queue-capacity=100",
            prefix + ".allow-core-thread-time-out=true",
            prefix + ".keep-alive=60s")
        .run(
            context -> {
              ToolkitProperties properties = context.getBean(ToolkitProperties.class);
              ToolkitProperties.ThreadExecutionOptions options = properties.getThreadPoolExecutor();

              assertThat(options.getDaemon()).isFalse();
              assertThat(options.getCorePoolSize()).isEqualTo(3);
              assertThat(options.getThreadNamePrefix()).isEqualTo("thread-");
              assertThat(options.getAwaitTermination()).isFalse();
              assertThat(options.getAwaitTerminationPeriod()).isEqualTo(Duration.ofSeconds(10));
              assertThat(options.getMaximumPoolSize()).isEqualTo(10);
              assertThat(options.getQueueCapacity()).isEqualTo(100);
              assertThat(options.getAllowCoreThreadTimeOut()).isTrue();
              assertThat(options.getKeepAlive()).isEqualTo(Duration.ofSeconds(60));
            });
  }
}
