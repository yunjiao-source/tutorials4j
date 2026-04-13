package tutorials4j.framework.autoconfigure.servlet;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import tutorials4j.framework.core.constants.BasePropertiesConstants;
import tutorials4j.framework.core.servlet.CachedBodyFilter;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link CachedBodyConfig} 单元测试
 *
 * @author Yun Jiao
 */
public class CachedBodyConfigTest {
    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        String prefix = BasePropertiesConstants.PROPERTY_PREFIX_SERVLET_CACHE_BODY + ".";
        applicationContextRunner = new ApplicationContextRunner()
                .withUserConfiguration(CachedBodyConfig.class)
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
                    FilterRegistrationBean<CachedBodyFilter> registration = context.getBean(FilterRegistrationBean.class);
                    assertThat(registration).isNotNull();

                    // 验证注册信息
                    assertThat(registration.getUrlPatterns())
                            .containsExactly("/api/*", "/admin/*");
                    assertThat(registration.getOrder()).isEqualTo(1);
                    assertThat(registration.getFilterName()).isEqualTo("customCachedBodyFilter");
                    assertThat(registration.determineDispatcherTypes())
                            .isEqualTo(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));

                    // 验证 Filter 内部的 maxContentLength 被正确设置
                    CachedBodyFilter filter = registration.getFilter();
                    java.lang.reflect.Field field = CachedBodyFilter.class.getDeclaredField("maxContentLength");
                    field.setAccessible(true);
                    long maxLen = (long) field.get(filter);
                    assertThat(maxLen).isEqualTo(2L * 1024 * 1024);
                });
    }
}
