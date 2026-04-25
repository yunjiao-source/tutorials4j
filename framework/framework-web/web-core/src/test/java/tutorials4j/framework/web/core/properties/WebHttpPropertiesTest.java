package tutorials4j.framework.web.core.properties;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import tutorials4j.framework.common.core.PropertiesConsts;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WebHttpProperties} 的单元测试，验证配置属性绑定，包括嵌套的 {@link WebHttpProperties.CachedRequestBody}。
 *
 * @author Yun Jiao (generated test)
 */
class WebHttpPropertiesTest {
    private final static String PREFIX = PropertiesConsts.PROPERTY_PREFIX_WEB_HTTP;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Configuration
    @EnableConfigurationProperties(WebHttpProperties.class)
    static class TestConfig {
        // 启用配置属性绑定
    }

    @Test
    void testDefaultValues() {
        contextRunner.run(context -> {
            WebHttpProperties props = context.getBean(WebHttpProperties.class);
            WebHttpProperties.CachedRequestBody cached = props.getCachedRequestBody();

            assertThat(cached.isEnabled()).isFalse();
            assertThat(cached.getUrlPatterns()).containsExactly("/**");
            assertThat(cached.getOrder()).isEqualTo(1);
            assertThat(cached.getName()).isEqualTo("defaultCachedBodyFilter");
            assertThat(cached.getDispatcherTypes()).isEqualTo(EnumSet.allOf(DispatcherType.class));
            assertThat(cached.getMaxContentLength()).isEqualTo(DataSize.ofMegabytes(2));
        });
    }

    @Test
    void testBindCachedRequestBodyEnabled() {
        contextRunner.withPropertyValues(PREFIX + ".cached-request-body.enabled=true")
                .run(context -> {
                    WebHttpProperties props = context.getBean(WebHttpProperties.class);
                    assertThat(props.getCachedRequestBody().isEnabled()).isTrue();
                });
    }

    @Test
    void testBindUrlPatterns() {
        contextRunner.withPropertyValues(
                PREFIX + ".cached-request-body.url-patterns=/api/**,/admin/*"
        ).run(context -> {
            WebHttpProperties props = context.getBean(WebHttpProperties.class);
            assertThat(props.getCachedRequestBody().getUrlPatterns())
                    .containsExactly("/api/**", "/admin/*");
        });
    }

    @Test
    void testBindOrderAndName() {
        contextRunner.withPropertyValues(
                PREFIX + ".cached-request-body.order=5",
                PREFIX + ".cached-request-body.name=customCachedBodyFilter"
        ).run(context -> {
            WebHttpProperties props = context.getBean(WebHttpProperties.class);
            assertThat(props.getCachedRequestBody().getOrder()).isEqualTo(5);
            assertThat(props.getCachedRequestBody().getName()).isEqualTo("customCachedBodyFilter");
        });
    }

    @Test
    void testBindDispatcherTypes() {
        contextRunner.withPropertyValues(
                PREFIX + ".cached-request-body.dispatcher-types=REQUEST,FORWARD"
        ).run(context -> {
            WebHttpProperties props = context.getBean(WebHttpProperties.class);
            EnumSet<DispatcherType> expected = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
            assertThat(props.getCachedRequestBody().getDispatcherTypes()).isEqualTo(expected);
        });
    }

    @Test
    void testBindMaxContentLength() {
        contextRunner.withPropertyValues(
                PREFIX + ".cached-request-body.max-content-length=10MB"
        ).run(context -> {
            WebHttpProperties props = context.getBean(WebHttpProperties.class);
            assertThat(props.getCachedRequestBody().getMaxContentLength()).isEqualTo(DataSize.ofMegabytes(10));
        });
    }

    @Test
    void testAllCustomBindingsTogether() {
        contextRunner.withPropertyValues(
                PREFIX + ".cached-request-body.enabled=true",
                PREFIX + ".cached-request-body.url-patterns=/upload/**",
                PREFIX + ".cached-request-body.order=10",
                PREFIX + ".cached-request-body.name=uploadBodyFilter",
                PREFIX + ".cached-request-body.dispatcher-types=REQUEST,ERROR",
                PREFIX + ".cached-request-body.max-content-length=5MB"
        ).run(context -> {
            WebHttpProperties props = context.getBean(WebHttpProperties.class);
            WebHttpProperties.CachedRequestBody cached = props.getCachedRequestBody();
            assertThat(cached.isEnabled()).isTrue();
            assertThat(cached.getUrlPatterns()).containsExactly("/upload/**");
            assertThat(cached.getOrder()).isEqualTo(10);
            assertThat(cached.getName()).isEqualTo("uploadBodyFilter");
            assertThat(cached.getDispatcherTypes()).containsExactly(DispatcherType.REQUEST, DispatcherType.ERROR);
            assertThat(cached.getMaxContentLength()).isEqualTo(DataSize.ofMegabytes(5));
        });
    }
}