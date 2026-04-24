package tutorials4j.framework.data.hibernate.tenant;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ContextConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tutorials4j.framework.data.core.properties.DataTenantProperties;
import tutorials4j.framework.data.hibernate.autoconfigure.HibernateTenantConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link HibernateTenantConfiguration} 单元测试
 *
 * @author Yun Jiao
 */
class HibernateDataTenantConfigurationTest1 {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HibernateTenantConfiguration.class))
            .withUserConfiguration(TestDataSourceConfig.class);

    // ========== 基础 Bean 测试 ==========
    @Test
    void shouldRegisterDefaultCurrentTenantIdentifierResolver() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
        });
    }

    // ========== DATABASE 租户类型 + 不同数据源 ==========
    @Test
    void shouldRegisterHikariMultiTenantConnectionProviderWhenHikariDataSourceIsPrimary() {
        contextRunner
                .withPropertyValues("tutorials4j.data.tenant.type=DATABASE")
                .withUserConfiguration(HikariDataSourceConfig.class)
                .run(assertSingleBean(HikariMultiTenantConnectionProvider.class));
    }

    @Test
    void shouldRegisterDruidMultiTenantConnectionProviderWhenDruidDataSourceIsPrimary() {
        contextRunner
                .withPropertyValues("tutorials4j.data.tenant.type=DATABASE")
                .withUserConfiguration(DruidDataSourceConfig.class)
                .run(assertSingleBean(DruidMultiTenantConnectionProvider.class));
    }

    @Test
    void shouldRegisterDbcp2MultiTenantConnectionProviderWhenDbcp2DataSourceIsPrimary() {
        contextRunner
                .withPropertyValues("tutorials4j.data.tenant.type=DATABASE")
                .withUserConfiguration(Dbcp2DataSourceConfig.class)
                .run(assertSingleBean(Dbcp2MultiTenantConnectionProvider.class));
    }

    // ========== 条件未满足时不应注册 ==========
    @Test
    void shouldNotRegisterAnyMultiTenantConnectionProviderWhenTenantTypeIsNotDatabase() {
        contextRunner
                .withPropertyValues("tutorials4j.data.tenant.type=SCHEMA")
                .withUserConfiguration(HikariDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    @Test
    void shouldNotRegisterHikariProviderWhenNoSingleCandidateHikariDataSource() {
        // 两个 HikariDataSource 破坏 @ConditionalOnSingleCandidate
        contextRunner
                .withPropertyValues("tutorials4j.data.tenant.type=DATABASE")
                .withUserConfiguration(MultipleHikariDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    // 但其他类型的数据源可能依然存在，此处仅验证 Hikari 未注册
                });
    }

    // ========== 辅助方法 ==========
    private <T> ContextConsumer<AssertableApplicationContext> assertSingleBean(Class<T> beanType) {
        return context -> {
            assertThat(context).hasSingleBean(beanType);
            T provider = context.getBean(beanType);
            assertThat(provider).isNotNull();
        };
    }

    // ========== 内部配置类 ==========
    @Configuration
    static class TestDataSourceConfig {
        @Bean
        public DataTenantProperties tenantProperties() {
            return new DataTenantProperties();
        }
    }

    @Configuration
    static class HikariDataSourceConfig {
        @Bean
        @Primary  // 确保只有一个候选者
        public HikariDataSource hikariDataSource() {
            return new HikariDataSource();
        }
    }

    @Configuration
    static class DruidDataSourceConfig {
        @Bean
        @Primary
        public DruidDataSource druidDataSource() {
            return new DruidDataSource();
        }
    }

    @Configuration
    static class Dbcp2DataSourceConfig {
        @Bean
        @Primary
        public BasicDataSource basicDataSource() {
            return new BasicDataSource();
        }
    }

    @Configuration
    static class MultipleHikariDataSourceConfig {
        @Bean
        public HikariDataSource hikariDataSource1() {
            return new HikariDataSource();
        }

        @Bean
        public HikariDataSource hikariDataSource2() {
            return new HikariDataSource();
        }
    }
}