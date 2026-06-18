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
 * 缓存核心配置类。
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
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE-CORE] Cache Configuration");
  }

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

  @Bean
  @ConditionalOnMissingBean
  LocalLockService localLockService(LockCacheProperties properties) {
    log.trace("[CACHE-CORE] Local Lock Service");
    return new LocalLockService(properties.getLocal());
  }

  @Bean
  @ConditionalOnMissingBean
  LocalLockableAspect localLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      LocalLockService localLockService) {
    log.trace("[CACHE-CORE] Local Lockable Aspect");
    return new LocalLockableAspect(spelMethodBasedExpressionEvaluator, localLockService);
  }
}
