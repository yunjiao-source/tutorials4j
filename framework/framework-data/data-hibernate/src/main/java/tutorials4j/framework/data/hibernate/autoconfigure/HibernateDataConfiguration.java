package tutorials4j.framework.data.hibernate.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tutorials4j.framework.common.spring.util.SecurityUtils;
import tutorials4j.framework.data.hibernate.properties.HibernateDataProperties;
import tutorials4j.framework.data.hibernate.spi.CacheRegionHibernatePropertiesCustomizer;

/**
 * Hibernate 数据访问自动配置类。
 *
 * <p>开启 JPA 审计（{@link EnableJpaAuditing}），并注册审计人提供者与缓存区域属性定制器等 Bean， 用于集成 Spring Data JPA 与
 * Hibernate。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
@EnableConfigurationProperties({HibernateDataProperties.class})
public class HibernateDataConfiguration {
  /** 初始化：输出 Hibernate 数据访问配置已加载的跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA-HIBERNATE] Data Hibernate Configuration");
  }

  /**
   * 注册审计人提供者，从当前安全上下文中获取登录账号作为 JPA 审计人。
   *
   * @return 当前登录账号的审计人提供者
   */
  @Bean
  AuditorAware<String> simpleAuditorProvider() {
    log.trace("[DATA-HIBERNATE] simple Auditor Provider");
    return SecurityUtils::getAccountOptional;
  }

  /**
   * 注册缓存区域 Hibernate 属性定制器，将 Hibernate 数据访问属性应用到二级缓存区域。
   *
   * @param properties Hibernate 数据访问属性
   * @return 缓存区域属性定制器
   */
  @Bean
  CacheRegionHibernatePropertiesCustomizer cacheRegionHibernatePropertiesCustomizer(
      HibernateDataProperties properties) {
    log.trace("[DATA-HIBERNATE] Cache Region Hibernate Properties Customizer");
    return new CacheRegionHibernatePropertiesCustomizer(properties);
  }
}
