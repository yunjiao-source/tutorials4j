package tutorials4j.framework.web.core.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.lang.PropertiesConsts;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WebClientProperties} 单元测试
 *
 * @author Yun Jiao
 */
class WebClientPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Configuration
    @EnableConfigurationProperties(WebClientProperties.class)
    static class TestConfig {
        // 空配置，仅用于启动自动配置和属性绑定
    }

    @Test
    void testDefaultValues() {
        contextRunner.run(context -> {
            WebClientProperties properties = context.getBean(WebClientProperties.class);
            assertThat(properties.isLoggerEnabled()).isFalse();
            assertThat(properties.isBufferingClientHttpRequestEnabled()).isFalse();
            assertThat(properties.getBaseUrl()).isNull();
            assertThat(properties.getDefaultHeaders()).isEmpty();
        });
    }

    @Test
    void testCustomValues() {
        String prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT + ".";
        contextRunner
                .withPropertyValues(
                        prefix + "logger-enabled=true",
                        prefix + "buffering-client-http-request-enabled=true",
                        prefix + "base-url=http://example.com",
                        prefix + "default-headers.X-Request-Id=123",
                        prefix + "default-headers.Authorization=Bearer token"
                )
                .run(context -> {
                    WebClientProperties properties = context.getBean(WebClientProperties.class);
                    assertThat(properties.isLoggerEnabled()).isTrue();
                    assertThat(properties.isBufferingClientHttpRequestEnabled()).isTrue();
                    assertThat(properties.getBaseUrl()).isEqualTo("http://example.com");
                    assertThat(properties.getDefaultHeaders())
                            .containsExactlyInAnyOrderEntriesOf(
                                    Map.of("X-Request-Id", "123", "Authorization", "Bearer token")
                            );
                });
    }

    @Test
    void testPartialCustomValues() {
        String prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT + ".";
        contextRunner
                .withPropertyValues(
                        prefix + "base-url=http://localhost:8080"
                )
                .run(context -> {
                    WebClientProperties properties = context.getBean(WebClientProperties.class);
                    assertThat(properties.getBaseUrl()).isEqualTo("http://localhost:8080");
                    // 其他属性应保持默认值
                    assertThat(properties.isLoggerEnabled()).isFalse();
                    assertThat(properties.getDefaultHeaders()).isEmpty();
                });
    }
}