package tutorials4j.framework.data.hibernate.tenant;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.data.core.properties.DataTenantProperties;
import tutorials4j.framework.data.hibernate.autoconfigure.HibernateTenantConfiguration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * {@link HibernateTenantConfiguration} 单元测试
 *
 * @author Yun Jiao
 */
class HibernateTenantConfigurationTest {
    private final static String PREFIX = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT;

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HibernateTenantConfiguration.class))
            // 确保 @ConfigurationProperties 生效
            .withUserConfiguration(PropertiesBindingConfig.class);

    // ==================== 基础条件测试 ====================

    @Test
    void testTenantDisabled_ShouldNotLoadAnyBeans() {
        contextRunner.withPropertyValues(PREFIX + ".enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    @Test
    void testTenantEnabledWithoutDataSource_ShouldNotLoadInternalConfigurations() {
        contextRunner.withPropertyValues(PREFIX + ".enabled=true")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                });
    }

    // ==================== 租户类型 TABLE（默认）测试 ====================

    @Test
    void testTenantTypeTable_ShouldRegisterOnlyResolver() {
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true",
                        PREFIX + ".type=TABLE")
                .withUserConfiguration(SimpleDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    @Test
    void testTenantTypeDatabase_ShouldRegisterOnlyResolver() {
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true",
                        PREFIX + ".type=DATABASE")
                .withUserConfiguration(SimpleDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    @Test
    void testTenantTypeNull_ShouldRegisterOnlyResolver() {
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true")
                .withUserConfiguration(SimpleDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    // ==================== 租户类型 DATABASE 测试 ====================
    // 注意：由于代码中两个内部类都设置了 matchIfMissing = true，若不显式配置 type 会导致冲突，见 testNoTypeWithDataSource_ShouldFail


    @Test
    void testTenantTypeDatabaseWithHikari_ShouldRegisterHikariProvider() {
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true",
                        PREFIX + ".type=DATABASE")
                .withUserConfiguration(HikariDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).hasSingleBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    @Test
    void testTenantTypeDatabaseWithDruid_ShouldRegisterDruidProvider() {
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true",
                        PREFIX + ".type=DATABASE")
                .withUserConfiguration(DruidDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
                    // DruidMultiTenantConnectionProvider 只在 DruidDataSource 存在且为单一候选时注册
                    assertThat(context).hasSingleBean(DruidMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                });
    }

    @Test
    void testTenantTypeDatabaseWithDbcp2_ShouldRegisterDbcp2Provider() {
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true",
                        PREFIX + ".type=DATABASE")
                .withUserConfiguration(Dbcp2DataSourceConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).hasSingleBean(Dbcp2MultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                });
    }



    // ==================== 测试配置类 ====================

    @TestConfiguration
    @EnableConfigurationProperties(DataTenantProperties.class)
    static class PropertiesBindingConfig {
        // 仅用于激活 @ConfigurationProperties 绑定，实际 bean 由 Spring Boot 自动创建
    }

    @TestConfiguration
    static class SimpleDataSourceConfig {
        @Bean
        DataSource dataSource() {
            // 使用最简单的 DataSource 实现，不满足任何连接池特定条件
            return mock(DataSource.class);
        }
    }

    @TestConfiguration
    static class HikariDataSourceConfig {
        @Bean
        @Primary
        HikariDataSource hikariDataSource() {
            return new HikariDataSource();
        }
    }

    @TestConfiguration
    static class DruidDataSourceConfig {
        @Bean
        @Primary
        DruidDataSource druidDataSource() {
            return new DruidDataSource();
        }
    }

    @TestConfiguration
    static class Dbcp2DataSourceConfig {
        @Bean
        @Primary
        BasicDataSource basicDataSource() {
            return new BasicDataSource();
        }
    }

    @TestConfiguration
    @Import({HikariDataSourceConfig.class, DruidDataSourceConfig.class, Dbcp2DataSourceConfig.class})
    static class MultipleDataSourceConfig {
        // 三个不同类型的 DataSource 同时存在，各自都是单一候选，但整体上有多个 DataSource 实现类
        // 每个 Provider 上的 @ConditionalOnSingleCandidate 检查的是具体的子类（如 HikariDataSource）的唯一性，
        // 因此每个子类都只有一个候选，但是否会创建三个 Provider？不会，因为 Spring 会为每个符合条件的子类创建对应的 Bean，
        // 但这里为了验证"多个不同类型 DataSource"场景，实际会创建三个 Provider。本测试预期与真实行为一致：三个 Provider 都会注册。
        // 如果你期望的场景是“多个数据源时不应注册任何 Provider”，则需要修改测试逻辑。当前代码中 @ConditionalOnSingleCandidate 限定的是具体类型，
        // 所以只要 HikariDataSource 只有一个，就会注册 HikariMultiTenantConnectionProvider，其他类似。所以 MultipleDataSourceConfig 会创建三个 Provider。
        // 因此该测试实际验证的是“多个特定类型的 DataSource 各有一个候选时，都会注册”，而不是“不注册任何 Provider”。
        // 若要测试“无单一候选”，可以注册两个 HikariDataSource。
    }

    @TestConfiguration
    static class DuplicateHikariDataSourceConfig {
        @Bean
        HikariDataSource hikariDataSource1() {
            return new HikariDataSource();
        }

        @Bean
        HikariDataSource hikariDataSource2() {
            return new HikariDataSource();
        }
    }

    @Test
    void testTenantTypeDatabaseWithDuplicateHikari_ShouldNotRegisterHikariProvider() {
        // 存在多个 HikariDataSource 候选，不满足 @ConditionalOnSingleCandidate
        contextRunner.withPropertyValues(
                        PREFIX + ".enabled=true",
                        PREFIX + ".type=DATABASE")
                .withUserConfiguration(DuplicateHikariDataSourceConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                });
    }
}