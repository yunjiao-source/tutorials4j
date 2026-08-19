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
 * 租户核心自动配置：注册租户任务装饰器与租户拦截器，并将租户拦截器注册到 Spring Web 拦截器链中。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableConfigurationProperties({TenantProperties.class})
public class TenantConfiguration implements WebMvcConfigurer {
  private final TenantProperties properties;

  /** 初始化日志输出 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[TENANT-CORE] Tenant Configuration");
  }

  /**
   * 注册组合任务装饰器 Bean（容器中不存在时生效），用于向异步任务传递租户上下文。
   *
   * @param creator 组合任务装饰器创建器
   * @return 组合任务装饰器
   */
  @Bean
  @ConditionalOnMissingBean
  CompositeTaskDecorator compositeTaskDecorator(CompositeTaskDecoratorCreator creator) {
    log.trace("[TENANT-CORE] Composite Task Decorator");
    return creator.getInstance();
  }

  /**
   * 注册租户任务装饰器创建器 Bean（容器中不存在时生效），供异步任务传递租户上下文。
   *
   * @return 租户任务装饰器创建器
   */
  @Bean
  @ConditionalOnMissingBean
  TaskDecoratorCreator tenantTaskDecoratorCreator() {
    log.trace("[TENANT-CORE] Tenant Task Decorator Creator");
    return TenantTaskDecorator::new;
  }

  /**
   * 注册租户拦截器，并按配置的包含/排除路径进行拦截。
   *
   * @param registry 拦截器注册表
   */
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
