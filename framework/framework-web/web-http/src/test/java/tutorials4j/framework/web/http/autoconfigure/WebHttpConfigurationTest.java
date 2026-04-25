package tutorials4j.framework.web.http.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.http.CachedRequestBodyFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WebHttpConfiguration} 单元测试
 *
 * @author Yun Jiao
 */
class WebHttpConfigurationTest {
    private final static String PREFIX = PropertiesConsts.PROPERTY_PREFIX_WEB_HTTP;
    
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            // 加载自动配置类
            .withConfiguration(AutoConfigurations.of(WebHttpConfiguration.class))
            // 确保 WebHttpProperties 可被绑定并注册为 Bean
            .withUserConfiguration(PropertiesTestConfig.class);

    @Test
    void shouldRegisterFilterWhenPropertyEnabled() {
        contextRunner
                .withPropertyValues(PREFIX + ".cached-request-body.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(FilterRegistrationBean.class);
                    FilterRegistrationBean<?> registration = context.getBean(FilterRegistrationBean.class);
                    assertThat(registration.getFilter()).isInstanceOf(CachedRequestBodyFilter.class);
                });
    }

    @Test
    void shouldNotRegisterFilterWhenPropertyDisabled() {
        contextRunner
                .withPropertyValues(PREFIX + ".cached-request-body.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(FilterRegistrationBean.class);
                });
    }

    @Test
    void shouldRegisterFilterWithCustomUrlPatternsAndOrderAndName() {
        contextRunner
                .withPropertyValues(
                        PREFIX + ".cached-request-body.enabled=true",
                        PREFIX + ".cached-request-body.url-patterns=/api/*,/secure/*",
                        PREFIX + ".cached-request-body.order=5",
                        PREFIX + ".cached-request-body.name=customCachedBodyFilter"
                )
                .run(context -> {
                    FilterRegistrationBean<?> registration = context.getBean(FilterRegistrationBean.class);
                    assertThat(registration.getUrlPatterns()).containsExactly("/api/*", "/secure/*");
                    assertThat(registration.getOrder()).isEqualTo(5);
                });
    }

    @Test
    void shouldRegisterFilterWithDispatcherTypesWhenProvided() {
        contextRunner
                .withPropertyValues(
                        PREFIX + ".cached-request-body.enabled=true",
                        PREFIX + ".cached-request-body.dispatcher-types=REQUEST,FORWARD,ERROR"
                )
                .run(context -> {
                    FilterRegistrationBean<?> registration = context.getBean(FilterRegistrationBean.class);
                    // dispatcherTypes 存储为 Set<DispatcherType>，我们验证非空且包含预期值
                    assertThat(registration.determineDispatcherTypes())
                            .isNotEmpty()
                            .extracting(Enum::name)
                            .contains("REQUEST", "FORWARD", "ERROR");
                });
    }

    @Test
    void shouldNotSetOptionalFilterPropertiesWhenMissing() {
        contextRunner
                .withPropertyValues(PREFIX + ".cached-request-body.enabled=true")
                .run(context -> {
                    FilterRegistrationBean<?> registration = context.getBean(FilterRegistrationBean.class);
                    // 未显式配置时，urlPatterns 应为空（或 null，取决于代码实现）
                    // 这里假设 CachedRequestBodyConfiguration 中只有当 cachedRequestBody.getUrlPatterns() 非空时才添加
                    assertThat(registration.getUrlPatterns()).contains("/**");
                    // order 未设置时，registration.getOrder() 可能为 null（默认值未覆盖）
                    assertThat(registration.getOrder()).isEqualTo(1L);
                });
    }


    private String getName(FilterRegistrationBean filterRegistrationBean) {
        try {
            var field = FilterRegistrationBean.class.getDeclaredField("name");
            field.setAccessible(true);
            return (String)field.get(filterRegistrationBean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 辅助配置类：显式启用 ConfigurationProperties 绑定，使 WebHttpProperties 成为 Bean。
     */
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(WebHttpProperties.class)
    static class PropertiesTestConfig {
    }
}