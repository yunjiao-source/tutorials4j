package tutorials4j.framework.web.client.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequest;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.web.core.properties.WebClientProperties;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WebRestConfiguration} 单元测试
 *
 * @author Yun Jiao
 */
class WebRestConfigurationTest {
    private final static String PREFIX = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebRestConfiguration.class))
            .withUserConfiguration(TestWebClientPropertiesConfig.class);

    @Test
    void testDefaultConfiguration() {
        contextRunner.run(context -> {
            // 默认 headers 相关 Bean 应存在
            assertThat(context).hasSingleBean(RestTemplateRequestCustomizer.class);
            assertThat(context).hasBean("defaultHeadersRestTemplateRequestCustomizer");

            assertThat(context).hasSingleBean(RestClientCustomizer.class);
            assertThat(context.getBeansOfType(RestClientCustomizer.class)).hasSize(1);

            assertThat(context.getBeansOfType(WebClientCustomizer.class)).hasSize(2);

            // 日志拦截器相关 Bean 默认应不存在（logger 未配置）
            assertThat(context).doesNotHaveBean("logHeadersRestTemplateCustomizer");
            assertThat(context).doesNotHaveBean("logHeadersRestClientCustomizer");
            assertThat(context).doesNotHaveBean("logHeadersWebClientCustomizer");
        });
    }

    @Test
    void testLoggerEnabled() {
        contextRunner.withPropertyValues(PREFIX + ".logger=true")
                .run(context -> {
                    // 日志拦截器 Bean 应存在
                    assertThat(context).hasBean("logHeadersRestTemplateCustomizer");
                    assertThat(context).hasBean("logHeadersRestClientCustomizer");
                    assertThat(context).hasBean("logHeadersWebClientCustomizer");

                    // 验证 logHeadersRestTemplateCustomizer 添加了 LogClientHttpRequestInterceptor
                    RestTemplateCustomizer restTemplateCustomizer = context.getBean("logHeadersRestTemplateCustomizer", RestTemplateCustomizer.class);
                    // 无法直接验证 interceptor 添加，但可以确认 Bean 类型正确
                    assertThat(restTemplateCustomizer).isNotNull();

                    // 验证 logHeadersRestClientCustomizer
                    RestClientCustomizer restClientCustomizer = context.getBean("logHeadersRestClientCustomizer", RestClientCustomizer.class);
                    assertThat(restClientCustomizer).isNotNull();

                    // 验证 logHeadersWebClientCustomizer
                    WebClientCustomizer webClientCustomizer = context.getBean("logHeadersWebClientCustomizer", WebClientCustomizer.class);
                    assertThat(webClientCustomizer).isNotNull();
                });
    }

    @Test
    void testLoggerDisabled() {
        contextRunner.withPropertyValues(PREFIX + ".logger=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean("logHeadersRestTemplateCustomizer");
                    assertThat(context).doesNotHaveBean("logHeadersRestClientCustomizer");
                    assertThat(context).doesNotHaveBean("logHeadersWebClientCustomizer");
                });
    }

    @Test
    void testDefaultHeadersCustomizers() {
        Map<String, String> testHeaders = new HashMap<>();
        testHeaders.put("X-Test-Header", "test-value");
        contextRunner.withPropertyValues(
                PREFIX + ".default-headers.X-Test-Header=test-value"
        ).run(context -> {
            // defaultHeadersRestTemplateRequestCustomizer 应使用 properties 中的 headers
            RestTemplateRequestCustomizer<ClientHttpRequest> requestCustomizer =
                    context.getBean("defaultHeadersRestTemplateRequestCustomizer", RestTemplateRequestCustomizer.class);
            assertThat(requestCustomizer).isNotNull();

            // defaultHeadersRestClientCustomizer 应存在
            RestClientCustomizer restClientCustomizer = context.getBean("defaultHeadersRestClientCustomizer", RestClientCustomizer.class);
            assertThat(restClientCustomizer).isNotNull();

            // defaultHeadersWebClientCustomizer 应存在
            WebClientCustomizer webClientCustomizer = context.getBean("defaultHeadersWebClientCustomizer", WebClientCustomizer.class);
            assertThat(webClientCustomizer).isNotNull();

            // defaultWebClientCustomizer 始终存在
            WebClientCustomizer defaultWebClientCustomizer = context.getBean("defaultWebClientCustomizer", WebClientCustomizer.class);
            assertThat(defaultWebClientCustomizer).isNotNull();
        });
    }

    // 模拟 WebClientProperties 配置，支持从环境属性绑定
    @Configuration(proxyBeanMethods = false)
    static class TestWebClientPropertiesConfig {

        @Bean
        public WebClientProperties webClientProperties() {
            return new WebClientProperties();
        }
    }
}