package tutorials4j.framework.web.core.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WebClientProperties} 的单元测试，验证配置属性绑定。
 *
 * @author Yun Jiao
 */
class WebClientPropertiesTest {
    private final static String PREFIX = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Configuration
    @EnableConfigurationProperties(WebClientProperties.class)
    static class TestConfig {
        // 仅用于启用 ConfigurationProperties 绑定
    }

    @Test
    void testDefaultValues() {
        contextRunner.run(context -> {
            WebClientProperties props = context.getBean(WebClientProperties.class);
            assertThat(props.isLogger()).isFalse();
            assertThat(props.getDefaultHeaders()).isNotNull().isEmpty();
        });
    }

    @Test
    void testBindLoggerEnabled() {
        contextRunner.withPropertyValues(PREFIX + ".logger=true")
                .run(context -> {
                    WebClientProperties props = context.getBean(WebClientProperties.class);
                    assertThat(props.isLogger()).isTrue();
                });
    }

    @Test
    void testBindDefaultHeaders() {
        contextRunner.withPropertyValues(
                PREFIX + ".default-headers.Content-Type=application/json",
                PREFIX + ".default-headers.X-Request-Id=123"
        ).run(context -> {
            WebClientProperties props = context.getBean(WebClientProperties.class);
            Map<String, String> headers = props.getDefaultHeaders();
            assertThat(headers).containsEntry("Content-Type", "application/json")
                               .containsEntry("X-Request-Id", "123");
        });
    }

    @Test
    void testOverwriteDefaultHeaders() {
        contextRunner.withPropertyValues(
                PREFIX + ".default-headers.Accept=text/plain"
        ).run(context -> {
            WebClientProperties props = context.getBean(WebClientProperties.class);
            // 默认的 Map 是空的，绑定后只应包含设置的项
            assertThat(props.getDefaultHeaders()).hasSize(1)
                                                 .containsEntry("Accept", "text/plain");
        });
    }
}