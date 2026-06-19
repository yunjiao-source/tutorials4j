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
 * Hibernate配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
@EnableConfigurationProperties({HibernateDataProperties.class})
public class HibernateDataConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[DATA-HIBERNATE] Data Hibernate Configuration");
  }

  @Bean
  AuditorAware<String> simpleAuditorProvider() {
    log.trace("[DATA-HIBERNATE] simple Auditor Provider");
    return SecurityUtils::getAccountOptional;
  }

  @Bean
  CacheRegionHibernatePropertiesCustomizer cacheRegionHibernatePropertiesCustomizer(
      HibernateDataProperties properties) {
    log.trace("[DATA-HIBERNATE] Cache Region Hibernate Properties Customizer");
    return new CacheRegionHibernatePropertiesCustomizer(properties);
  }
}
