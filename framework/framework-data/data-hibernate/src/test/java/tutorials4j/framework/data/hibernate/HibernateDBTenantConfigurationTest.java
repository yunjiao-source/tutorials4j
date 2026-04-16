package tutorials4j.framework.data.hibernate;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tutorials4j.framework.data.core.tenant.TenantProperties;
import tutorials4j.framework.data.hibernate.tenant.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class HibernateDBTenantConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(HibernateDBTenantConfiguration.class);

    @Test
    void whenNoDataSource_thenNoTenantProviderBeans() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
            assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
            assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
        });
    }

    @Test
    void withHikariDataSourceAndTenantProperties_shouldCreateProviderBasedOnClasspath() {
        contextRunner.withBean(HikariDataSource.class, () -> mock(HikariDataSource.class))
                .withBean(TenantProperties.class, () -> mock(TenantProperties.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                });
    }

    @Test
    void withDruidDataSourceAndTenantProperties_shouldCreateProviderBasedOnClasspath() {
        contextRunner.withBean(DruidDataSource.class, () -> mock(DruidDataSource.class))
                .withBean(TenantProperties.class, () -> mock(TenantProperties.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(Dbcp2MultiTenantConnectionProvider.class);
                    assertThat(context).hasSingleBean(DruidMultiTenantConnectionProvider.class);
                });
    }

    @Test
    void withDbcp2DataSourceAndTenantProperties_shouldCreateProviderBasedOnClasspath() {
        contextRunner.withBean(BasicDataSource.class, () -> mock(BasicDataSource.class))
                .withBean(TenantProperties.class, () -> mock(TenantProperties.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).hasSingleBean(Dbcp2MultiTenantConnectionProvider.class);
                    assertThat(context).doesNotHaveBean(DruidMultiTenantConnectionProvider.class);
                });
    }
}
