package tutorials4j.service.system.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.toolkit.data.hibernate.domain.QueryRepositoryImpl;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EntityScan(basePackages = "tutorials4j.feature.basic.entity")
@EnableJpaRepositories(
    basePackages = "tutorials4j.feature.basic.repository",
    repositoryBaseClass = QueryRepositoryImpl.class)
public class SystemServiceConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[SERVICE-SYSTEM] System Service Configuration");
  }
}
