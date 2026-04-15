package tutorials4j.framework.web.core.servlet;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import tutorials4j.framework.web.core.constant.WebPropertiesConsts;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link CachedRequestBodyConfiguration} 单元测试
 *
 * @author Yun Jiao
 */
public class CachedRequestBodyConfigurationTest {
    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        String prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CACHED_REQUEST_BODY + ".";
        applicationContextRunner = new ApplicationContextRunner()
                .withUserConfiguration(CachedRequestBodyConfiguration.class)
                .withPropertyValues(
                        prefix + "max-content-length=2MB",
                        prefix + "url-patterns=/api/*,/admin/*",
                        prefix + "order=1",
                        prefix + "name=customCachedBodyFilter",
                        prefix + "dispatcher-types=REQUEST,FORWARD"
                );;
    }

    @Test
    public void testFilterRegistrationBeanCreatedWithProperties() {
        applicationContextRunner
                .run(context -> {
                    // 验证 Bean 存在
                    FilterRegistrationBean<CachedRequestBodyFilter> registration = context.getBean(FilterRegistrationBean.class);
                    assertThat(registration).isNotNull();

                    // 验证注册信息
                    assertThat(registration.getUrlPatterns())
                            .containsExactly("/api/*", "/admin/*");
                    assertThat(registration.getOrder()).isEqualTo(1);
                    assertThat(registration.getFilterName()).isEqualTo("customCachedBodyFilter");
                    assertThat(registration.determineDispatcherTypes())
                            .isEqualTo(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
                });
    }
}
