package tutorials4j.framework.oauth.satoken.autoconfigure;

import cn.dev33.satoken.filter.SaServletFilter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.oauth.satoken.autoconfigure.SaTokenOAuthProperties.SaServletFilterOptions;
import tutorials4j.framework.oauth.satoken.common.SaServletFilterCustomizer;
import tutorials4j.framework.oauth.satoken.component.SaApiKeyTemplateFactory;
import tutorials4j.framework.oauth.satoken.component.SaLogForSlf4j;
import tutorials4j.framework.oauth.satoken.component.SaTokenExceptionHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SaTokenOAuthProperties.class})
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_OAUTH_SA_TOKEN,
    name = "strategy",
    havingValue = "sa_token",
    matchIfMissing = true)
public class SaTokenOAuthConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[OAUTH-SA-TOKEN] Sa-Token Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_OAUTH_SA_TOKEN,
      name = "log-for-slf4j",
      havingValue = "true")
  SaLogForSlf4j saLogForSlf4j() {
    log.trace("[OAUTH-SA-TOKEN] Sa Log For Slf4j");
    return new SaLogForSlf4j();
  }

  @Bean
  @ConditionalOnMissingBean
  SaApiKeyTemplateFactory saApiKeyTemplateFactory() {
    log.trace("[OAUTH-SA-TOKEN] Sa Api Key Template Factory");
    return new SaApiKeyTemplateFactory();
  }

  @Bean
  SaTokenExceptionHandler saTokenExceptionHandler() {
    log.trace("[OAUTH-SA-TOKEN] Sa Token Exception Handler");
    return new SaTokenExceptionHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  SaServletFilter saServletFilter(
      SaTokenOAuthProperties properties, ObjectProvider<SaServletFilterCustomizer> customizers) {
    log.trace("[OAUTH-SA-TOKEN] Sa Servlet Filter");
    SaServletFilterOptions options = properties.getFilter();
    SaServletFilter filter = new SaServletFilter();
    if (ArrayUtils.isNotEmpty(options.getIncludePatterns())) {
      filter.addInclude(options.getIncludePatterns());
    }
    if (ArrayUtils.isNotEmpty(options.getExcludePatterns())) {
      filter.addExclude(options.getExcludePatterns());
    }
    customizers.orderedStream().forEach(customizer -> customizer.customize(filter));

    return filter;
  }
}
