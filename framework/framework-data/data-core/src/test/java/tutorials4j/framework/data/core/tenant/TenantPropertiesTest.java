package tutorials4j.framework.data.core.tenant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.core.DataPropertiesConsts;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 单元测试
 *
 * @author Yun Jiao
 */
public class TenantPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withUserConfiguration(TestConfig.class);

    // 配置类：启用目标配置属性
    @Configuration
    @EnableConfigurationProperties(TenantProperties.class)
    static class TestConfig {
    }

    @Test
    void testPropertiesBinding() {
        String prefix = DataPropertiesConsts.PROPERTY_PREFIX_DATA_TENANT + ".";
        contextRunner.withPropertyValues(
                        prefix + "pathPatterns=/admin/*,/log/*",

                        prefix + "datasource.db1.driverClassName=org.postgresql.Driver",
                        prefix + "datasource.db1.url=jdbc:postgresql://localhost:5432/demo",
                        prefix + "datasource.db1.username=postgres",
                        prefix + "datasource.db1.password=postgres",

                        prefix + "datasource.db2.driverClassName=com.mysql.cj.jdbc.Driver",
                        prefix + "datasource.db2.url=jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC",
                        prefix + "datasource.db2.username=root",
                        prefix + "datasource.db2.password=123456"
                )
                .run(context -> {
                    TenantProperties properties = context.getBean(TenantProperties.class);
                    assertThat(properties.getPathPatterns()).containsExactly("/admin/*", "/log/*");
                    assertThat(properties.getDatasource().size()).isEqualTo(2);

                    TenantProperties.DataSourceProperties db1Prop = properties.getDatasource().get("db1");
                    assertThat(db1Prop.getUrl()).isEqualTo("jdbc:postgresql://localhost:5432/demo");
                    assertThat(db1Prop.getUsername()).isEqualTo("postgres");
                    assertThat(db1Prop.getPassword()).isEqualTo("postgres");
                    assertThat(db1Prop.getDriverClassName()).isEqualTo("org.postgresql.Driver");

                    TenantProperties.DataSourceProperties db2Prop = properties.getDatasource().get("db2");
                    assertThat(db2Prop.getUrl()).isEqualTo("jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC");
                    assertThat(db2Prop.getUsername()).isEqualTo("root");
                    assertThat(db2Prop.getPassword()).isEqualTo("123456");
                    assertThat(db2Prop.getDriverClassName()).isEqualTo("com.mysql.cj.jdbc.Driver");
                });
    }
}
