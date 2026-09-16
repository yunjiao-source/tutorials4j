package tutorials4j.toolkit.data.hibernate.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tutorials4j.toolkit.core.util.SecurityUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@EnableJpaAuditing
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
