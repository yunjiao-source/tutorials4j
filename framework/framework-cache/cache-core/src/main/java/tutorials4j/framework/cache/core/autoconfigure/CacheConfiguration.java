package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.lock.LocalLockService;
import tutorials4j.framework.cache.core.lock.LocalLockableAspect;
import tutorials4j.framework.cache.core.properties.CacheProperties;
import tutorials4j.framework.cache.core.properties.LockCacheProperties;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * 缓存核心自动配置类。
 *
 * <p>启用缓存相关配置属性，并在缺少自定义 Bean 时自动注册缓存管理器创建器工厂、 本地锁服务与本地锁切面。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  CacheProperties.class,
  NamedCacheProperties.class,
  LockCacheProperties.class
})
public class CacheConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE-CORE] Cache Configuration");
  }

  /**
   * 注册缓存管理器创建器工厂 Bean，将容器中所有 {@link CacheManagerCreator} 按类别注入工厂。
   *
   * @param providers 全部缓存管理器创建器
   * @return 缓存管理器创建器工厂单例
   */
  @Bean
  @ConditionalOnMissingBean
  CacheManagerCreatorFactory cacheManagerCreatorFactory(List<CacheManagerCreator<?>> providers) {
    Map<CacheManagerCreatorCategory, CacheManagerCreator<?>> creators =
        providers.stream().collect(Collectors.toMap(CacheManagerCreator::getCategory, m -> m));
    CacheManagerCreatorFactory.instance.setCreatorMap(creators);

    log.trace(
        "[CACHE-CORE] Injected instances in CacheManagerCreatorFactory is {}", creators.keySet());
    return CacheManagerCreatorFactory.instance;
  }

  /**
   * 注册本地锁服务 Bean。
   *
   * @param properties 锁缓存配置属性
   * @return 本地锁服务实例
   */
  @Bean
  @ConditionalOnMissingBean
  LocalLockService localLockService(LockCacheProperties properties) {
    log.trace("[CACHE-CORE] Local Lock Service");
    return new LocalLockService(properties.getLocal());
  }

  /**
   * 注册本地锁切面 Bean。
   *
   * @param spelMethodBasedExpressionEvaluator SpEL 表达式求值器
   * @param localLockService 本地锁服务
   * @return 本地锁切面实例
   */
  @Bean
  @ConditionalOnMissingBean
  LocalLockableAspect localLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      LocalLockService localLockService) {
    log.trace("[CACHE-CORE] Local Lockable Aspect");
    return new LocalLockableAspect(spelMethodBasedExpressionEvaluator, localLockService);
  }
}
