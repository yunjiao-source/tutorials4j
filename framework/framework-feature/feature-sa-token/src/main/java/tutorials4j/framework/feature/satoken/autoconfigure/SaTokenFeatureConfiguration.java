package tutorials4j.framework.feature.satoken.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tutorials4j.framework.feature.satoken.component.SaApiKeyTemplateFactory;
import tutorials4j.framework.feature.satoken.service.ApiKeyRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ComponentScan(
    basePackages = {
      "tutorials4j.framework.feature.satoken.service",
      "tutorials4j.framework.feature.satoken.component",
      "tutorials4j.framework.feature.satoken.web"
    })
@EnableJpaRepositories(basePackages = {"tutorials4j.framework.feature.satoken.service"})
@EntityScan(basePackages = {"tutorials4j.framework.feature.satoken.model"})
public class SaTokenFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-SA-TOKEN] Sa-Token Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  SaApiKeyTemplateFactory saApiKeyTemplateFactory(ApiKeyRepository apiKeyRepository) {
    log.trace("[FEATURE-SA-TOKEN] Sa Api Key Template Factory");
    return new SaApiKeyTemplateFactory(apiKeyRepository);
  }
}
