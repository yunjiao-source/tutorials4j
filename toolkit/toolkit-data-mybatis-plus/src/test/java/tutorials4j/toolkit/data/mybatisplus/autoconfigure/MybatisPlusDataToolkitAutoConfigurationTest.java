package tutorials4j.toolkit.data.mybatisplus.autoconfigure;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.toolkit.core.constant.PropertyConsts;
import tutorials4j.toolkit.data.mybatisplus.component.AuditMetaObjectHandler;
import tutorials4j.toolkit.data.mybatisplus.component.IdentifierMybatisPlusPropertiesCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.BlockAttackInterceptorCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.MybatisPlusInterceptorCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.OptimisticLockerInterceptorCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.PaginationInnerInterceptorCustomizer;

/**
 * {@link MybatisPlusDataToolkitAutoConfiguration} 自动装配测试。
 *
 * @author YunJiao Yang
 */
class MybatisPlusDataToolkitAutoConfigurationTest {
  private static final String PREFIX = PropertyConsts.PROPERTY_TOOLKIT_DATA_MYBATIS_PLUS;
  private static final String AUTO_CONFIGURATION_IMPORTS =
      "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(MybatisPlusDataToolkitAutoConfiguration.class));

  @Configuration(proxyBeanMethods = false)
  static class CustomMetaObjectHandlerConfig {
    static final MetaObjectHandler CUSTOM = mock(MetaObjectHandler.class);

    @Bean
    MetaObjectHandler customMetaObjectHandler() {
      return CUSTOM;
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class CustomInterceptorConfig {
    static final MybatisPlusInterceptor CUSTOM = new MybatisPlusInterceptor();

    @Bean
    MybatisPlusInterceptor customMybatisPlusInterceptor() {
      return CUSTOM;
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class CustomCustomizerConfig {
    static final InnerInterceptor CUSTOM_INNER = mock(InnerInterceptor.class);

    @Bean
    MybatisPlusInterceptorCustomizer earliestCustomizer() {
      return new MybatisPlusInterceptorCustomizer() {
        @Override
        public void custom(MybatisPlusInterceptor interceptor) {
          interceptor.addInnerInterceptor(CUSTOM_INNER);
        }

        @Override
        public int getOrder() {
          return 50;
        }
      };
    }
  }

  @Test
  @DisplayName("装配属性、审计处理器、ID 生成器自定义器与拦截器自定义器")
  void shouldRegisterBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(MybatisPlusDataToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(MybatisPlusDataToolkitProperties.class);
          assertThat(context).hasSingleBean(MetaObjectHandler.class);
          assertThat(context).hasSingleBean(MybatisPlusPropertiesCustomizer.class);
          assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
          assertThat(context).hasSingleBean(PaginationInnerInterceptorCustomizer.class);
          assertThat(context).hasSingleBean(OptimisticLockerInterceptorCustomizer.class);
          assertThat(context).hasSingleBean(BlockAttackInterceptorCustomizer.class);

          assertThat(context.getBean(MetaObjectHandler.class))
              .isInstanceOf(AuditMetaObjectHandler.class);
          assertThat(context.getBean(MybatisPlusPropertiesCustomizer.class))
              .isInstanceOf(IdentifierMybatisPlusPropertiesCustomizer.class);
        });
  }

  @Test
  @DisplayName("按 100/200/300 的顺序组装内置拦截器")
  void shouldAssembleBuiltInInterceptorsInOrder() {
    contextRunner.run(
        context -> {
          MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);

          assertThat(interceptor.getInterceptors())
              .hasSize(3)
              .satisfies(
                  interceptors -> {
                    assertThat(interceptors.get(0)).isInstanceOf(PaginationInnerInterceptor.class);
                    assertThat(interceptors.get(1))
                        .isInstanceOf(OptimisticLockerInnerInterceptor.class);
                    assertThat(interceptors.get(2)).isInstanceOf(BlockAttackInnerInterceptor.class);
                  });
        });
  }

  @Test
  @DisplayName("分页拦截器使用配置的数据库类型")
  void shouldUseConfiguredDbType() {
    contextRunner
        .withPropertyValues(PREFIX + ".db-type=h2")
        .run(
            context -> {
              MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);
              PaginationInnerInterceptor pagination =
                  (PaginationInnerInterceptor) interceptor.getInterceptors().get(0);

              assertThat(pagination.getDbType()).isEqualTo(DbType.H2);
            });
  }

  @Test
  @DisplayName("自定义自定义器按顺序号插入到内置拦截器之前")
  void shouldOrderCustomCustomizersWithBuiltInOnes() {
    contextRunner
        .withUserConfiguration(CustomCustomizerConfig.class)
        .run(
            context -> {
              MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);

              assertThat(interceptor.getInterceptors()).hasSize(4);
              assertThat(interceptor.getInterceptors().get(0))
                  .isSameAs(CustomCustomizerConfig.CUSTOM_INNER);
            });
  }

  @Test
  @DisplayName("用户自定义 MetaObjectHandler 时自动装配退让")
  void shouldBackOffWhenUserDefinesMetaObjectHandler() {
    contextRunner
        .withUserConfiguration(CustomMetaObjectHandlerConfig.class)
        .run(
            context -> {
              assertThat(context.getBean(MetaObjectHandler.class))
                  .isSameAs(CustomMetaObjectHandlerConfig.CUSTOM);
            });
  }

  @Test
  @DisplayName("用户自定义 MybatisPlusInterceptor 时自动装配退让且不再组装内置拦截器")
  void shouldBackOffWhenUserDefinesInterceptor() {
    contextRunner
        .withUserConfiguration(CustomInterceptorConfig.class)
        .run(
            context -> {
              assertThat(context.getBean(MybatisPlusInterceptor.class))
                  .isSameAs(CustomInterceptorConfig.CUSTOM);
              assertThat(CustomInterceptorConfig.CUSTOM.getInterceptors()).isEmpty();
            });
  }

  @Test
  @DisplayName("类标注 @AutoConfiguration")
  void shouldBeAnnotatedAsAutoConfiguration() {
    assertThat(MybatisPlusDataToolkitAutoConfiguration.class.getAnnotation(AutoConfiguration.class))
        .isNotNull();
  }

  @Test
  @DisplayName("已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() throws IOException {
    try (InputStream stream =
        getClass().getClassLoader().getResourceAsStream(AUTO_CONFIGURATION_IMPORTS)) {
      assertThat(stream).isNotNull();
      assertThat(new String(stream.readAllBytes(), UTF_8))
          .contains(MybatisPlusDataToolkitAutoConfiguration.class.getName());
    }
  }
}
