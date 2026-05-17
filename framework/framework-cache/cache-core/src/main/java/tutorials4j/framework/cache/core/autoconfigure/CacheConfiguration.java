package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.lock.LocalLockService;
import tutorials4j.framework.cache.core.lock.LocalLockableAspect;
import tutorials4j.framework.cache.core.properties.CacheProperties;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;
import tutorials4j.framework.common.core.content.SpelMethodBasedExpressionEvaluator;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  CacheProperties.class,
  NamedCacheProperties.class,
})
public class CacheConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CACHE-CORE] Cache Core Configuration");
  }

  @Bean
  CacheManagerCreatorFactory cacheManagerCreatorFactory(
      List<CacheManagerCreator<?>> cacheManagerCreators) {
    log.debug("[CACHE-CORE] Cache Manager Creator Factory");
    CacheManagerCreatorFactory factory = new CacheManagerCreatorFactory();
    factory.setCacheManagerCreators(cacheManagerCreators);

    log.debug("[CACHE-CORE] 工厂'CacheManagerCreatorFactory'注入实例：{}", cacheManagerCreators);
    return factory;
  }

  @Bean
  @ConditionalOnMissingBean
  LocalLockService localLockService() {
    log.debug("[CACHE-CORE] Local Lock Service");
    return new LocalLockService();
  }

  @Bean
  @ConditionalOnMissingBean
  LocalLockableAspect localLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      LocalLockService localLockService) {
    log.debug("[CACHE-CORE] Local Lockable Aspect");
    return new LocalLockableAspect(spelMethodBasedExpressionEvaluator, localLockService);
  }
}
