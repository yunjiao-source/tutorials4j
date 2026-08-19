package tutorials4j.framework.data.mybatis.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.data.mybatis.AuditMetaObjectHandler;
import tutorials4j.framework.data.mybatis.UidentifierGenerator;
import tutorials4j.framework.data.mybatis.customizer.BlockAttackInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.customizer.MybatisPlusInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.customizer.OptimisticLockerInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.customizer.PaginationInnerInterceptorCustomizer;
import tutorials4j.framework.data.mybatis.properties.MybatisPlusDataProperties;

/**
 * MyBatis Plus 自动配置类。
 *
 * <p>负责装配 MyBatis Plus 拦截器（分页、乐观锁、防全表更新/删除）以及审计字段填充处理器、 默认主键生成器等组件，可通过配置属性控制各拦截器的启用开关。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({MybatisPlusDataProperties.class})
public class MybatisPlusDataConfiguration {
  /** 启动后打印初始化日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA-MYBATIS-PLUS] Data Mybatis Plus Configuration");
  }

  /**
   * 装配 MyBatis Plus 拦截器，并按排序后的顺序应用所有自定义器。
   *
   * @param customizers 拦截器自定义器提供者，包含各拦截器自定义器 Bean
   * @return 装配完成的 MyBatis Plus 拦截器
   */
  @Bean
  @ConditionalOnMissingBean
  MybatisPlusInterceptor mybatisPlusInterceptor(
      ObjectProvider<MybatisPlusInterceptorCustomizer> customizers) {
    log.trace("[DATA-MYBATIS-PLUS] Mybatis Plus Interceptor");
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    customizers.orderedStream().forEach(customizer -> customizer.custom(interceptor));
    return interceptor;
  }

  /**
   * 装配分页拦截器自定义器（默认开启，可通过配置关闭）。
   *
   * @param properties MyBatis Plus 数据访问配置属性，用于获取数据库类型
   * @return 分页拦截器自定义器
   */
  @Bean
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS,
      name = "interceptors.pagination",
      havingValue = "true",
      matchIfMissing = true)
  MybatisPlusInterceptorCustomizer paginationInnerInterceptorCustomizer(
      MybatisPlusDataProperties properties) {
    log.trace("[DATA-MYBATIS-PLUS] Pagination Inner Interceptor Customizer");
    return new PaginationInnerInterceptorCustomizer(properties.getDbType());
  }

  /**
   * 装配乐观锁拦截器自定义器（默认开启，可通过配置关闭）。
   *
   * @return 乐观锁拦截器自定义器
   */
  @Bean
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS,
      name = "interceptors.optimistic-locker",
      havingValue = "true",
      matchIfMissing = true)
  MybatisPlusInterceptorCustomizer optimisticLockerInnerInterceptorCustomizer() {
    log.trace("[DATA-MYBATIS-PLUS] Optimistic Locker Inner Interceptor Customizer");
    return new OptimisticLockerInterceptorCustomizer();
  }

  /**
   * 装配防全表更新/删除拦截器自定义器（默认开启，可通过配置关闭）。
   *
   * @return 防全表更新/删除拦截器自定义器
   */
  @Bean
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS,
      name = "interceptors.block-attack",
      havingValue = "true",
      matchIfMissing = true)
  MybatisPlusInterceptorCustomizer blockAttackInnerInterceptorCustomizer() {
    log.trace("[DATA-MYBATIS-PLUS] Block Attack Inner Interceptor Customizer");
    return new BlockAttackInterceptorCustomizer();
  }

  /**
   * 装配默认主键生成器配置，将全局主键生成器设置为框架统一 UID 生成器。
   *
   * @return MyBatis Plus 属性自定义器
   */
  @Bean
  MybatisPlusPropertiesCustomizer defaultIdentifierGeneratorMybatisPlusPropertiesCustomizer() {
    log.trace(
        "[DATA-MYBATIS-PLUS] Default Identifier Generator Mybatis Plus Properties Customizer");
    return plusProperties ->
        // plusProperties.getGlobalConfig().setIdentifierGenerator(new
        // SnowflakeIdentifierGenerator());
        plusProperties.getGlobalConfig().setIdentifierGenerator(new UidentifierGenerator());
  }

  /**
   * 装配审计字段自动填充处理器。
   *
   * @return 审计字段自动填充处理器
   */
  @Bean
  @ConditionalOnMissingBean
  MetaObjectHandler auditMetaObjectHandler() {
    log.trace("[DATA-MYBATIS-PLUS] Audit Meta Object Handler");
    return new AuditMetaObjectHandler();
  }
}
