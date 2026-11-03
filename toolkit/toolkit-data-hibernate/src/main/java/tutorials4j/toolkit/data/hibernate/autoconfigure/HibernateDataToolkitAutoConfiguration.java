package tutorials4j.toolkit.data.hibernate.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.toolkit.core.constant.PropertyConsts;
import tutorials4j.toolkit.core.util.SecurityUtils;
import tutorials4j.toolkit.data.hibernate.domain.QueryRepositoryImpl;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@EnableJpaAuditing
// @Conditional(JpaBasePackagesCondition.class)
@EntityScan(basePackages = PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA_ENTITY_BASE_PACKAGES)
@EnableJpaRepositories(
    basePackages = PropertyConsts.PROPERTY_TOOLKIT_DATA_JPA_REPOSITORY_BASE_PACKAGES,
    repositoryBaseClass = QueryRepositoryImpl.class)
@EnableConfigurationProperties({HibernateDataToolkitProperties.class})
public class HibernateDataToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-DATA-HIBERNATE] Hibernate Data Toolkit Auto Configuration");
  }

  @Bean
  AuditorAware<String> simpleAuditorAware() {
    log.trace("[TOOLKIT-DATA-HIBERNATE] Simple Auditor Aware");
    return SecurityUtils::getAccountOptional;
  }
}
