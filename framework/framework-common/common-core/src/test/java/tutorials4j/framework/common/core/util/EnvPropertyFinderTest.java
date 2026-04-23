package tutorials4j.framework.common.core.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link EnvPropertyFinder} 单元测试
 *
 * @author Yun Jiao
 */
public class EnvPropertyFinderTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            // 模拟配置文件中的属性
            .withPropertyValues(
                    "test.string=hello",
                    "test.bool=true",
                    "test.number=42"
            );


    @Test
    void testGetPropertyFromEnvironment() {
        runner.run(context -> {
            Environment env = context.getEnvironment();

            // 测试 String 属性
            String stringValue = EnvPropertyFinder.getProperty(env, "test.string", String.class);
            assertThat(stringValue).isEqualTo("hello");

            // 测试带默认值的 String
            String missingWithDefault = EnvPropertyFinder.getProperty(env, "missing.key", String.class, "default");
            assertThat(missingWithDefault).isEqualTo("default");

            // 测试 Boolean
            Boolean boolValue = EnvPropertyFinder.getProperty(env, "test.bool", Boolean.class);
            assertThat(boolValue).isTrue();
        });
    }

    @Test
    void testGetStringPropertyViaConditionContext() {
        // 注意：ConditionContext 通常只在 @Conditional 内部使用，但这里为了演示，我们仍可模拟
        // ApplicationContextRunner 不直接提供 ConditionContext，但我们可以通过 context 获取 Environment
        // 如果需要测试 ConditionContext 相关方法，建议直接 mock，因为 ConditionContext 不是普通 Bean。
        // 对于 ConditionContext 方法，用 Mockito 更简单。
        // 这里仅演示 Environment 和 ApplicationContext 路径。
        runner.run(context -> {
            // 通过 ApplicationContext 获取 Environment
            String value = EnvPropertyFinder.getStringProperty(context, "test.string");
            assertThat(value).isEqualTo("hello");

            Boolean flag = EnvPropertyFinder.getBoolProperty(context, "test.bool", false);
            assertThat(flag).isTrue();
        });
    }
}
