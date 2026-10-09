package tutorials4j.toolkit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * {@link SpringUtils} 单元测试：继承自 Hutool {@code SpringUtil} 的静态容器访问能力。
 *
 * @author Yun Jiao
 */
class SpringUtilsTest {
  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @Configuration(proxyBeanMethods = false)
  @Import(SpringUtils.class)
  static class TestConfig {
    static final AtomicReference<String> RECEIVED_EVENT = new AtomicReference<>();

    @Bean
    String sampleBean() {
      return "sample-value";
    }

    @Bean
    ApplicationListener<SampleEvent> sampleEventListener() {
      return event -> RECEIVED_EVENT.set(event.getPayload());
    }
  }

  static class SampleEvent extends ApplicationEvent {
    private final String payload;

    SampleEvent(String payload) {
      super(payload);
      this.payload = payload;
    }

    String getPayload() {
      return payload;
    }
  }

  @Test
  @DisplayName("注册后可通过静态方法按名称获取 Bean")
  void shouldGetBeanByName() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(SpringUtils.class);

          Object bean = SpringUtils.getBean("sampleBean");

          assertThat(bean).isEqualTo("sample-value");
        });
  }

  @Test
  @DisplayName("可按类型以及名称 + 类型获取 Bean")
  void shouldGetBeanByType() {
    contextRunner.run(
        context -> {
          assertThat(SpringUtils.getBean(String.class)).isEqualTo("sample-value");
          assertThat(SpringUtils.getBean("sampleBean", String.class)).isEqualTo("sample-value");
        });
  }

  @Test
  @DisplayName("静态持有 ApplicationContext 与 BeanFactory")
  void shouldExposeApplicationContext() {
    contextRunner.run(
        context -> {
          assertThat(SpringUtils.getApplicationContext()).isNotNull();
          assertThat(SpringUtils.getBeanFactory()).isSameAs(context.getBeanFactory());
        });
  }

  @Test
  @DisplayName("可按类型列举 Bean 名称与实例")
  void shouldListBeansByType() {
    contextRunner.run(
        context -> {
          assertThat(SpringUtils.getBeanNamesForType(String.class)).contains("sampleBean");
          assertThat(SpringUtils.getBeansOfType(String.class))
              .containsEntry("sampleBean", "sample-value");
        });
  }

  @Test
  @DisplayName("可读取环境属性")
  void shouldReadEnvironmentProperty() {
    contextRunner
        .withPropertyValues("test.property=property-value")
        .run(
            context -> {
              assertThat(SpringUtils.getProperty("test.property")).isEqualTo("property-value");
              assertThat(SpringUtils.getProperty("missing.property", "fallback"))
                  .isEqualTo("fallback");
            });
  }

  @Test
  @DisplayName("可动态注册与注销单例 Bean")
  void shouldRegisterAndUnregisterBean() {
    contextRunner.run(
        context -> {
          SpringUtils.registerBean("dynamicBean", "dynamic-value");
          assertThat((Object) SpringUtils.getBean("dynamicBean")).isEqualTo("dynamic-value");

          SpringUtils.unregisterBean("dynamicBean");

          assertThat(context.getBeanFactory().containsSingleton("dynamicBean")).isFalse();
        });
  }

  @Test
  @DisplayName("可发布事件并被监听器接收")
  void shouldPublishEvent() {
    TestConfig.RECEIVED_EVENT.set(null);

    contextRunner.run(
        context -> {
          SpringUtils.publishEvent(new SampleEvent("payload"));

          assertThat(TestConfig.RECEIVED_EVENT).hasValue("payload");
        });
  }
}
