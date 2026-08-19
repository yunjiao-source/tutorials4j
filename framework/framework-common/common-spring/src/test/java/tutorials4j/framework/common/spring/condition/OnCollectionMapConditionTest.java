package tutorials4j.framework.common.spring.condition;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * {@link ConditionalOnMapProperty} 与 {@link OnCollectionMapCondition} 集成测试。
 *
 * <p>使用 {@link ApplicationContextRunner} 模拟不同的配置环境，验证 Map 属性条件的各种匹配场景。
 *
 * @author Yun Jiao
 */
class OnCollectionMapConditionTest {

  /** 用于模拟 Spring 应用上下文的测试运行器，默认加载 {@link TestConfig}。 */
  private final ApplicationContextRunner runner =
      new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(TestConfig.class));

  // ==================== 被测试的配置类 ====================
  /** 被测试的配置类：声明了各种 Map 条件场景下的 Bean。 */
  @Configuration(proxyBeanMethods = false)
  static class TestConfig {
    @Bean
    @ConditionalOnMapProperty(prefix = "my", name = "map", isEmpty = false, matchIfMissing = false)
    String nonEmptyMapBean() {
      return "nonEmptyMapBean";
    }

    // 使用合法的属性名：empty-map（kebab-case）
    @Bean
    @ConditionalOnMapProperty(
        prefix = "my",
        name = "empty-map",
        isEmpty = true,
        matchIfMissing = false)
    String emptyMapBean() {
      return "emptyMapBean";
    }

    @Bean
    @ConditionalOnMapProperty(
        prefix = "missing",
        name = "prop",
        isEmpty = false,
        matchIfMissing = true)
    String missingWithMatchIfMissingTrueBean() {
      return "missingWithMatchIfMissingTrueBean";
    }

    @Bean
    @ConditionalOnMapProperty(
        prefix = "missing",
        name = "prop",
        isEmpty = false,
        matchIfMissing = false)
    String missingWithMatchIfMissingFalseBean() {
      return "missingWithMatchIfMissingFalseBean";
    }

    @Bean
    @ConditionalOnMapProperty(
        prefix = "alias",
        value = "custom-name",
        isEmpty = false,
        matchIfMissing = false)
    String aliasBean() {
      return "aliasBean";
    }

    @Bean
    @ConditionalOnMapProperty(prefix = "only-prefix", isEmpty = false, matchIfMissing = false)
    String onlyPrefixBean() {
      return "onlyPrefixBean";
    }
  }

  // ==================== 测试场景 ====================

  /** Map 非空且 {@code isEmpty=false} 时，条件应匹配。 */
  @Test
  void nonEmptyMapShouldMatchWhenIsEmptyFalse() {
    runner
        .withPropertyValues("my.map.key1=value1", "my.map.key2=value2")
        .run(
            context -> {
              assertThat(context).hasBean("nonEmptyMapBean");
              assertThat(context.getBean("nonEmptyMapBean")).isEqualTo("nonEmptyMapBean");
            });
  }

  /** Map 为空且 {@code isEmpty=true} 时，条件应匹配。 */
  @Test
  void emptyMapShouldMatchWhenIsEmptyTrue() {
    // 模拟一个空 Map：创建一个 PropertySource，其中 "my.empty-map" 键对应的值为空 Map
    runner
        .withInitializer(
            applicationContext -> {
              MutablePropertySources sources =
                  applicationContext.getEnvironment().getPropertySources();
              Map<String, Object> emptyMapSource = new HashMap<>();
              emptyMapSource.put("my.empty-map", Collections.emptyMap());
              sources.addFirst(new MapPropertySource("emptyMapTestSource", emptyMapSource));
            })
        .run(
            context -> {
              assertThat(context).hasBean("emptyMapBean");
              assertThat(context.getBean("emptyMapBean")).isEqualTo("emptyMapBean");
            });
  }

  /** 属性缺失且 {@code matchIfMissing=true} 时，条件应匹配。 */
  @Test
  void missingMapShouldMatchWhenMatchIfMissingTrue() {
    runner.run(
        context -> {
          assertThat(context).hasBean("missingWithMatchIfMissingTrueBean");
          assertThat(context.getBean("missingWithMatchIfMissingTrueBean"))
              .isEqualTo("missingWithMatchIfMissingTrueBean");
        });
  }

  /** 属性缺失且 {@code matchIfMissing=false} 时，条件不应匹配。 */
  @Test
  void missingMapShouldNotMatchWhenMatchIfMissingFalse() {
    runner.run(
        context -> {
          assertThat(context).doesNotHaveBean("missingWithMatchIfMissingFalseBean");
        });
  }

  /** 使用 {@code value} 作为 {@code name} 的别名时，条件应匹配。 */
  @Test
  void valueAliasShouldWorkWhenNameIsBlank() {
    runner
        .withPropertyValues("alias.custom-name.key=value")
        .run(
            context -> {
              assertThat(context).hasBean("aliasBean");
              assertThat(context.getBean("aliasBean")).isEqualTo("aliasBean");
            });
  }

  /** 仅指定 {@code prefix} 且其下存在子属性时，条件应匹配。 */
  @Test
  void onlyPrefixShouldMatchWhenPrefixHasSubProperties() {
    runner
        .withPropertyValues("only-prefix.any-key=anyValue")
        .run(
            context -> {
              assertThat(context).hasBean("onlyPrefixBean");
              assertThat(context.getBean("onlyPrefixBean")).isEqualTo("onlyPrefixBean");
            });
  }

  /** 仅指定 {@code prefix} 且其下没有子属性时，条件不应匹配。 */
  @Test
  void onlyPrefixShouldNotMatchWhenNoSubProperties() {
    runner.run(
        context -> {
          assertThat(context).doesNotHaveBean("onlyPrefixBean");
        });
  }

  // ==================== 边界测试：同时指定 name 和 value，name 优先 ====================
  /** 边界测试配置：同时指定 {@code name} 与 {@code value}，用于验证 {@code name} 优先。 */
  @Configuration(proxyBeanMethods = false)
  static class NameOverridesValueConfig {
    @Bean
    @ConditionalOnMapProperty(
        prefix = "test",
        name = "explicit-name",
        value = "ignored-value",
        isEmpty = false)
    String bean() {
      return "bean";
    }
  }

  /** 同时指定 {@code name} 与 {@code value} 且 {@code name} 匹配时，条件应匹配。 */
  @Test
  void nameOverridesValue() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(NameOverridesValueConfig.class))
        .withPropertyValues("test.explicit-name.key=value")
        .run(context -> assertThat(context).hasBean("bean"));
  }

  /** 仅 {@code value} 匹配而 {@code name} 不匹配时，条件不应匹配。 */
  @Test
  void nameOverridesValue_shouldNotMatchWhenOnlyValueMatches() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(NameOverridesValueConfig.class))
        .withPropertyValues("test.ignored-value.key=value")
        .run(context -> assertThat(context).doesNotHaveBean("bean"));
  }
}
