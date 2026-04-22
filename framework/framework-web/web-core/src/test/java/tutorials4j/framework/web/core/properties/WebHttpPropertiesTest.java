package tutorials4j.framework.web.core.properties;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.lang.PropertiesConsts;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * {@link WebHttpProperties} 单元测试
 *
 * @author Yun Jiao
 */
public class WebHttpPropertiesTest {
    // 构建 Spring 上下文运行器，用于测试配置属性绑定
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TestConfig.class);

    // 配置类：启用目标配置属性
    @Configuration
    @EnableConfigurationProperties(WebHttpProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesBinding() {
        String prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_HTTP + ".cached-request-body.";
        contextRunner.withPropertyValues(
                        prefix + "max-content-length=4MB",
                        prefix + "url-patterns=/api/*,/admin/*",
                        prefix + "order=999",
                        prefix + "name=customCachedBodyFilter",
                        prefix + "dispatcher-types=REQUEST,FORWARD"
                )
                .run(context -> {
                    WebHttpProperties.CachedRequestBody properties = context.getBean(WebHttpProperties.class).getCachedRequestBody();
                    assertThat(properties.getMaxContentLength().toBytes()).isEqualTo(4194304L);
                    assertThat(properties.getUrlPatterns()).containsExactly("/api/*", "/admin/*");
                    assertThat(properties.getOrder()).isEqualTo(999);
                    assertThat(properties.getName()).isEqualTo("customCachedBodyFilter");
                    assertThat(properties.getDispatcherTypes()).isEqualTo(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
                });
    }
}
