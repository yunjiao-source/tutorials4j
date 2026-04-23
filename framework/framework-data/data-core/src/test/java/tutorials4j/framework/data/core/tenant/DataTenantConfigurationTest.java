package tutorials4j.framework.data.core.tenant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import tutorials4j.framework.common.core.bean.TenantContextHolder;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.data.core.autoconfigure.DataTenantConfiguration;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
class DataTenantConfigurationTest {

    // 1. 测试普通应用上下文（非Web环境），主要验证配置类和属性绑定
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataTenantConfiguration.class))
            // 模拟 application.properties 中的配置项
            .withPropertyValues(
                    "tutorials4j.data.tenant.path-patterns=/api/*,/admin/*"
            );

    // 2. 测试 Web 应用上下文（因为 TenantConfiguration 实现了 WebMvcConfigurer）
    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataTenantConfiguration.class))
            .withPropertyValues("tutorials4j.data.tenant.path-patterns=/api/*");

    @Test
    void contextLoads() {
        contextRunner.run(context -> {
            // 验证配置类本身存在
            assertThat(context).hasSingleBean(DataTenantConfiguration.class);
            // 验证 TenantProperties bean 存在且值绑定正确
            DataTenantProperties properties = context.getBean(DataTenantProperties.class);
            assertThat(properties.getPathPatterns()).containsExactly("/api/*", "/admin/*");
        });
    }

    @Test
    void tenantHandlerInterceptorIsRegistered() {
        webContextRunner.run(context -> {
            // 获取拦截器注册器（实际由 Spring 内部调用 addInterceptors 方法）
            //InterceptorRegistry registry = context.getBean(InterceptorRegistry.class);
            // 注意：InterceptorRegistry 不是普通 bean，通常需要从 WebMvcConfigurer 的调用中捕获
            // 更可靠的方法是检查 HandlerInterceptor 是否被注册到 Spring 的映射处理器中。
            // 这里采用另一种方式：手动模拟 addInterceptors 调用，验证拦截器被添加。
            // 方案：直接拿到 TenantConfiguration 实例，调用其 addInterceptors，然后断言 registry 中的内容。
            DataTenantConfiguration config = context.getBean(DataTenantConfiguration.class);
            TestInterceptorRegistry testRegistry = new TestInterceptorRegistry();
            config.addInterceptors(testRegistry);

            assertThat(testRegistry.getInterceptor()).isInstanceOf(TenantHandlerInterceptor.class);
            assertThat(testRegistry.getPathPatterns()).containsExactly("/api/*");
        });
    }

    @Test
    void interceptorActuallyInterceptsRequests() {
        String tenantCode = "demo";
        webContextRunner.run(context -> {
            // 由于 WebApplicationContextRunner 会创建模拟的 Spring MVC 环境，
            // 我们可以直接获取 DispatcherServlet 或 MockMvc 来测试拦截器行为，
            // 但更轻量的做法是手动构造拦截器并调用 preHandle。
            TenantHandlerInterceptor interceptor = new TenantHandlerInterceptor();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(DefaultConsts.HTTP_HEADER_TENANT, tenantCode);
            MockHttpServletResponse response = new MockHttpServletResponse();

            // 假设拦截器逻辑会从 header 中提取租户 ID，这里只是示例断言
            boolean result = interceptor.preHandle(request, response, new Object());
            // 根据你的 TenantHandlerInterceptor 实现编写具体断言
            assertThat(result).isTrue();
            assertThat(TenantContextHolder.get()).isEqualTo(tenantCode.toUpperCase());
        });
    }

    // 辅助类：用于记录 InterceptorRegistry 中添加的拦截器和路径
    private static class TestInterceptorRegistry extends InterceptorRegistry {
        private HandlerInterceptor interceptor;
        private String[] pathPatterns;

        @Override
        public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
            this.interceptor = interceptor;
            // 由于我们只关心 addPathPatterns 的参数，这里简单模拟
            return new InterceptorRegistration(interceptor) {
                @Override
                public InterceptorRegistration addPathPatterns(String... patterns) {
                    pathPatterns = patterns;
                    return this;
                }
            };
        }

        public HandlerInterceptor getInterceptor() {
            return interceptor;
        }

        public String[] getPathPatterns() {
            return pathPatterns;
        }
    }
}