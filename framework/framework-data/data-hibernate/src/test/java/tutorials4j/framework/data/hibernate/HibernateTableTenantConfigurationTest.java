package tutorials4j.framework.data.hibernate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tutorials4j.framework.data.hibernate.tenant.DefaultCurrentTenantIdentifierResolver;
import tutorials4j.framework.data.hibernate.tenant.HibernateTableTenantConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class HibernateTableTenantConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(HibernateTableTenantConfiguration.class);

    @Test
    void existBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DefaultCurrentTenantIdentifierResolver.class);
        });
    }

}
