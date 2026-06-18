package tutorials4j.framework.tenant.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.common.spring.core.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.spring.core.TaskDecoratorCreator;
import tutorials4j.framework.tenant.core.TenantHandlerInterceptor;
import tutorials4j.framework.tenant.core.TenantTaskDecorator;
import tutorials4j.framework.tenant.core.properties.TenantProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableConfigurationProperties({TenantProperties.class})
public class TenantConfiguration implements WebMvcConfigurer {
  private final TenantProperties properties;

  @PostConstruct
  public void postConstruct() {
    log.trace("[TENANT-CORE] Tenant Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CompositeTaskDecorator compositeTaskDecorator(CompositeTaskDecoratorCreator creator) {
    log.trace("[TENANT-CORE] Composite Task Decorator");
    return creator.getInstance();
  }

  @Bean
  @ConditionalOnMissingBean
  TaskDecoratorCreator tenantTaskDecoratorCreator() {
    log.trace("[TENANT-CORE] Tenant Task Decorator Creator");
    return TenantTaskDecorator::new;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    HandlerInterceptorOptions pathOptions = properties.getPath();
    TenantHandlerInterceptor interceptor = new TenantHandlerInterceptor();
    registry
        .addInterceptor(interceptor)
        .addPathPatterns(pathOptions.getIncludePathPatterns())
        .excludePathPatterns(pathOptions.getExcludePathPatterns());
    log.trace("[TENANT-CORE] Spring Web interceptor configuration parameters are {}", pathOptions);
  }
}
