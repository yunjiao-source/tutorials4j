package tutorials4j.toolkit.data.hibernate.autoconfigure;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * {@link HibernateDataToolkitAutoConfiguration} 自动装配测试。
 *
 * <p>{@code @EnableJpaAuditing} 需要 JPA 元模型，测试中以 mock 的 {@code jpaMappingContext} 替代 真实的
 * EntityManagerFactory。
 *
 * @author YunJiao Yang
 */
class HibernateDataToolkitAutoConfigurationTest {
  private static final String AUTO_CONFIGURATION_IMPORTS =
      "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(HibernateDataToolkitAutoConfiguration.class))
          .withUserConfiguration(JpaMappingContextTestConfig.class);

  @Configuration(proxyBeanMethods = false)
  static class JpaMappingContextTestConfig {
    @Bean
    MappingContext<?, ?> jpaMappingContext() {
      return mock(MappingContext.class);
    }
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("装配审计相关 Bean")
  void shouldRegisterAuditingBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(HibernateDataToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(AuditorAware.class);
          assertThat(context).hasBean("simpleAuditorAware");
          assertThat(context).hasBean("jpaAuditingHandler");
          assertThat(context).hasSingleBean(AuditingEntityListener.class);
        });
  }

  @Test
  @DisplayName("未登录时审计人为空")
  void shouldReturnEmptyAuditorWhenNotAuthenticated() {
    contextRunner.run(
        context -> {
          AuditorAware<String> auditorAware = auditorAware(context.getBean(AuditorAware.class));

          assertThat(auditorAware.getCurrentAuditor()).isEmpty();
        });
  }

  @Test
  @DisplayName("已登录时审计人取当前账号")
  void shouldReturnAuthenticatedAccountAsAuditor() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("alice", "credentials"));

    contextRunner.run(
        context -> {
          AuditorAware<String> auditorAware = auditorAware(context.getBean(AuditorAware.class));

          assertThat(auditorAware.getCurrentAuditor()).contains("alice");
        });
  }

  @Test
  @DisplayName("审计人跟随 SecurityContext 动态变化")
  void shouldResolveAuditorDynamically() {
    contextRunner.run(
        context -> {
          AuditorAware<String> auditorAware = auditorAware(context.getBean(AuditorAware.class));
          assertThat(auditorAware.getCurrentAuditor()).isEmpty();

          SecurityContextHolder.getContext()
              .setAuthentication(new UsernamePasswordAuthenticationToken("bob", "credentials"));

          assertThat(auditorAware.getCurrentAuditor()).contains("bob");
        });
  }

  @Test
  @DisplayName("直接调用 Bean 方法同样可得到审计人提供者")
  void shouldCreateAuditorAwareDirectly() {
    AuditorAware<String> auditorAware =
        new HibernateDataToolkitAutoConfiguration().simpleAuditorAware();

    assertThat(auditorAware.getCurrentAuditor()).isEmpty();
  }

  @Test
  @DisplayName("类标注 @AutoConfiguration 与 @EnableJpaAuditing")
  void shouldBeAnnotatedAsAutoConfigurationWithJpaAuditing() {
    assertThat(HibernateDataToolkitAutoConfiguration.class.getAnnotation(AutoConfiguration.class))
        .isNotNull();
    assertThat(HibernateDataToolkitAutoConfiguration.class.getAnnotation(EnableJpaAuditing.class))
        .isNotNull();
  }

  @Test
  @DisplayName("已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() throws IOException {
    try (InputStream stream =
        getClass().getClassLoader().getResourceAsStream(AUTO_CONFIGURATION_IMPORTS)) {
      assertThat(stream).isNotNull();
      assertThat(new String(stream.readAllBytes(), UTF_8))
          .contains(HibernateDataToolkitAutoConfiguration.class.getName());
    }
  }

  @SuppressWarnings("unchecked")
  private static AuditorAware<String> auditorAware(AuditorAware<?> auditorAware) {
    return (AuditorAware<String>) auditorAware;
  }
}
