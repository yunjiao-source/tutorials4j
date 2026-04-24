package tutorials4j.framework.data.core.tenant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;
import tutorials4j.framework.data.core.autoconfigure.DataTenantConfiguration;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
class DataTenantConfigurationTest {
    private final static String PREFIX = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataTenantConfiguration.class))
            .withUserConfiguration(PropertiesBindingConfig.class); // 确保 DataTenantProperties 能被绑定

    // 使用 WebApplicationContextRunner 模拟 Web 环境，因为 TenantWebMvcConfigurer 需注册拦截器
    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataTenantConfiguration.class))
            .withUserConfiguration(PropertiesBindingConfig.class);

    // ===================== 条件装配测试 =====================

    @Test
    void shouldNotLoadConfigurationWhenTenantDisabled() {
        webContextRunner
                .withPropertyValues(PREFIX + ".enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DataTenantConfiguration.TenantWebMvcConfigurer.class);
                    assertThat(context).doesNotHaveBean(TaskDecoratorSupplier.class);
                });
    }

    @Test
    void shouldLoadConfigurationWhenTenantEnabled() {
        webContextRunner
                .withPropertyValues(PREFIX + ".enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(DataTenantConfiguration.TenantWebMvcConfigurer.class);
                    assertThat(context).doesNotHaveBean(TaskDecoratorSupplier.class); // 无 CompositeTaskDecorator 时不创建
                });
    }

    @Test
    void shouldLoadTenantTaskDecoratorSupplierWhenCompositeTaskDecoratorExists() {
        contextRunner
                .withUserConfiguration(CompositeTaskDecoratorConfig.class)
                .withPropertyValues(PREFIX + ".enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(TaskDecoratorSupplier.class);
                    TaskDecoratorSupplier supplier = context.getBean(TaskDecoratorSupplier.class);
                    assertThat(supplier.get()).isInstanceOf(TenantTaskDecorator.class);
                });
    }

    @Test
    void shouldNotLoadTenantTaskDecoratorSupplierWhenCompositeTaskDecoratorMissing() {
        contextRunner
                .withPropertyValues(PREFIX + ".enabled=true")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(TaskDecoratorSupplier.class);
                });
    }


    // ===================== 辅助配置类 =====================

    /**
     * 启用 ConfigurationProperties 绑定，让 DataTenantProperties 可以被自动创建并填充属性
     */
    @Configuration
    @EnableConfigurationProperties(DataTenantProperties.class)
    static class PropertiesBindingConfig {
        // 仅用于激活 @ConfigurationProperties 处理
    }

    @Configuration
    static class CompositeTaskDecoratorConfig {
        @Bean
        CompositeTaskDecorator compositeTaskDecorator() {
            return new CompositeTaskDecorator(List.of());
        }
    }
}