package tutorials4j.toolkit.core.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import cn.hutool.extra.spring.SpringUtil;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.core.concurrent.ScheduledThreadPoolExecutorHolder;
import tutorials4j.toolkit.core.concurrent.ThreadPoolExecutorHolder;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
class ToolkitAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(ToolkitAutoConfiguration.class)
          .withUserConfiguration(TracerTestConfig.class)
          .withPropertyValues(minimalProperties());

  private static String[] minimalProperties() {
    String prefix = PropertyConsts.PROPERTY_TOOLKIT;
    return new String[] {
      prefix + ".scheduled-thread-pool-executor.daemon=true",
      prefix + ".scheduled-thread-pool-executor.core-pool-size=1",
      prefix + ".scheduled-thread-pool-executor.thread-name-prefix=test-scheduled-",
      prefix + ".scheduled-thread-pool-executor.await-termination=true",
      prefix + ".scheduled-thread-pool-executor.await-termination-period=1s",
      prefix + ".thread-pool-executor.daemon=true",
      prefix + ".thread-pool-executor.core-pool-size=1",
      prefix + ".thread-pool-executor.thread-name-prefix=test-thread-",
      prefix + ".thread-pool-executor.await-termination=true",
      prefix + ".thread-pool-executor.await-termination-period=1s",
      prefix + ".thread-pool-executor.maximum-pool-size=2",
      prefix + ".thread-pool-executor.queue-capacity=10",
      prefix + ".thread-pool-executor.allow-core-thread-time-out=false",
      prefix + ".thread-pool-executor.keep-alive=60s"
    };
  }

  @Test
  void shouldRegisterAllBeans() {
    contextRunner.run(
        context -> {
          assertThat(context.getBean(ToolkitProperties.class)).isNotNull();
          assertThat(context.getBean(SpringUtil.class)).isNotNull();
          assertThat(context.getBean(ScheduledThreadPoolExecutorHolder.class)).isNotNull();
          assertThat(context.getBean(ThreadPoolExecutorHolder.class)).isNotNull();
        });
  }

  @Test
  void shouldReturnSingletonHolderInstances() {
    contextRunner.run(
        context -> {
          assertThat(context.getBean(ScheduledThreadPoolExecutorHolder.class))
              .isSameAs(ScheduledThreadPoolExecutorHolder.instance);
          assertThat(context.getBean(ThreadPoolExecutorHolder.class))
              .isSameAs(ThreadPoolExecutorHolder.instance);
        });
  }

  @Test
  void shouldBindPropertiesAndInitializeHolders() {
    contextRunner.run(
        context -> {
          ToolkitProperties properties = context.getBean(ToolkitProperties.class);

          assertThat(properties.getScheduledThreadPoolExecutor().getCorePoolSize()).isEqualTo(1);
          assertThat(properties.getThreadPoolExecutor().getMaximumPoolSize()).isEqualTo(2);

          // 如果 Holder 提供了查询执行器内部状态的方法，可在此进一步断言
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
