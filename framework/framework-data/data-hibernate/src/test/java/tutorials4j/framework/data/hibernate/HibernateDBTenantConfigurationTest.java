package tutorials4j.framework.data.hibernate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tutorials4j.framework.data.core.tenant.TenantProperties;
import tutorials4j.framework.data.hibernate.tenant.DefaultCurrentTenantIdentifierResolver;
import tutorials4j.framework.data.hibernate.tenant.DruidMultiTenantConnectionProvider;
import tutorials4j.framework.data.hibernate.tenant.HibernateDBTenantConfiguration;
import tutorials4j.framework.data.hibernate.tenant.HikariMultiTenantConnectionProvider;

import javax.sql.DataSource;

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
    void withDataSourceAndTenantProperties_shouldCreateProviderBasedOnClasspath() {
        contextRunner.withBean(DataSource.class, () -> mock(DataSource.class))
                .withBean(TenantProperties.class, () -> mock(TenantProperties.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(HikariMultiTenantConnectionProvider.class);
                    assertThat(context).hasSingleBean(HikariMultiTenantConnectionProvider.class);
                });
    }
}
