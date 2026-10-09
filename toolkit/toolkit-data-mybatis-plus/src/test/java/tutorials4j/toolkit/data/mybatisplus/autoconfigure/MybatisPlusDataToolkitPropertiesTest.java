package tutorials4j.toolkit.data.mybatisplus.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * {@link MybatisPlusDataToolkitProperties} 配置绑定测试。
 *
 * @author Yun Jiao
 */
class MybatisPlusDataToolkitPropertiesTest {
  private static final String PREFIX = PropertyConsts.PROPERTY_TOOLKIT_DATA_MYBATIS_PLUS;

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(MybatisPlusDataToolkitProperties.class)
  static class TestConfig {}

  @Test
  @DisplayName("默认数据库类型为 MySQL")
  void shouldUseMysqlByDefault() {
    contextRunner.run(
        context -> {
          MybatisPlusDataToolkitProperties properties =
              context.getBean(MybatisPlusDataToolkitProperties.class);

          assertThat(properties.getDbType()).isEqualTo(DbType.MYSQL);
        });
  }

  @Test
  @DisplayName("可通过 db-type 绑定数据库类型")
  void shouldBindDbType() {
    contextRunner
        .withPropertyValues(PREFIX + ".db-type=postgresql")
        .run(
            context -> {
              MybatisPlusDataToolkitProperties properties =
                  context.getBean(MybatisPlusDataToolkitProperties.class);

              assertThat(properties.getDbType()).isEqualTo(DbType.POSTGRE_SQL);
            });
  }

  @Test
  @DisplayName("绑定非法数据库类型时上下文启动失败")
  void shouldFailOnInvalidDbType() {
    contextRunner
        .withPropertyValues(PREFIX + ".db-type=not-a-db")
        .run(context -> assertThat(context).hasFailed());
  }
}
