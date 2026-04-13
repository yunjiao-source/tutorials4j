package tutorials4j.framework.autoconfigure.servlet;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.core.constants.BasePropertiesConstants;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * {@link CachedBodyProperties} 单元测试
 *
 * @author Yun Jiao
 */
public class CachedBodyPropertiesTest {
    // 构建 Spring 上下文运行器，用于测试配置属性绑定
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TestConfig.class);

    // 配置类：启用目标配置属性
    @Configuration
    @EnableConfigurationProperties(CachedBodyProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesBinding() {
        String prefix = BasePropertiesConstants.PROPERTY_PREFIX_SERVLET_CACHE_BODY + ".";
        contextRunner.withPropertyValues(
                        prefix + "max-content-length=4MB",
                        prefix + "url-patterns=/api/*,/admin/*",
                        prefix + "order=999",
                        prefix + "name=customCachedBodyFilter",
                        prefix + "dispatcher-types=REQUEST,FORWARD"
                )
                .run(context -> {
                    CachedBodyProperties properties = context.getBean(CachedBodyProperties.class);
                    assertThat(properties.getMaxContentLength().toBytes()).isEqualTo(4194304L);
                    assertThat(properties.getUrlPatterns()).containsExactly("/api/*", "/admin/*");
                    assertThat(properties.getOrder()).isEqualTo(999);
                    assertThat(properties.getName()).isEqualTo("customCachedBodyFilter");
                    assertThat(properties.getDispatcherTypes()).isEqualTo(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
                });
    }
}
