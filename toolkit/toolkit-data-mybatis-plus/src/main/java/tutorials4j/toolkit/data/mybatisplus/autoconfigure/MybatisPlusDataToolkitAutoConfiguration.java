package tutorials4j.toolkit.data.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.data.mybatisplus.AuditMetaObjectHandler;
import tutorials4j.toolkit.data.mybatisplus.IdentifierMybatisPlusPropertiesCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.BlockAttackInterceptorCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.MybatisPlusInterceptorCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.OptimisticLockerInterceptorCustomizer;
import tutorials4j.toolkit.data.mybatisplus.interceptor.PaginationInnerInterceptorCustomizer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({MybatisPlusDataToolkitProperties.class})
public class MybatisPlusDataToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Mybatis-Plus Data Toolkit Auto Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  MetaObjectHandler auditMetaObjectHandler() {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Audit Meta Object Handler");
    return new AuditMetaObjectHandler();
  }

  @Bean
  MybatisPlusPropertiesCustomizer identifierMybatisPlusPropertiesCustomizer() {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Identifier Mybatis Plus Properties Customizer");
    return new IdentifierMybatisPlusPropertiesCustomizer();
  }

  @Bean
  @ConditionalOnMissingBean
  MybatisPlusInterceptor mybatisPlusInterceptor(
      ObjectProvider<MybatisPlusInterceptorCustomizer> customizers) {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Mybatis-Plus Interceptor");
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    customizers.orderedStream().forEach(customizer -> customizer.custom(interceptor));
    return interceptor;
  }

  @Bean
  MybatisPlusInterceptorCustomizer paginationInnerInterceptorCustomizer(
      MybatisPlusDataToolkitProperties properties) {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Pagination Inner Interceptor Customizer");
    return new PaginationInnerInterceptorCustomizer(properties.getDbType());
  }

  @Bean
  MybatisPlusInterceptorCustomizer optimisticLockerInnerInterceptorCustomizer() {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Optimistic Locker Inner Interceptor Customizer");
    return new OptimisticLockerInterceptorCustomizer();
  }

  @Bean
  MybatisPlusInterceptorCustomizer blockAttackInnerInterceptorCustomizer() {
    log.trace("[TOOLKIT-DATA-MYBATIS-PLUS] Block Attack Inner Interceptor Customizer");
    return new BlockAttackInterceptorCustomizer();
  }
}
