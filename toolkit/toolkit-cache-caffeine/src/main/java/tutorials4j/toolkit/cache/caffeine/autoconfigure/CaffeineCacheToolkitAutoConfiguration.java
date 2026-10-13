package tutorials4j.toolkit.cache.caffeine.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tutorials4j.toolkit.cache.caffeine.component.CaffeineCacheManagerCreator;
import tutorials4j.toolkit.cache.caffeine.component.CaffeineNamedCacheManager;
import tutorials4j.toolkit.cache.context.ConditionalOnCaffeineCacheType;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({
  CaffeineCacheToolkitProperties.class,
})
public class CaffeineCacheToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-CACHE-CAFFEINE] Caffeine Cache Toolkit Auto Configuration");
  }

  @Bean
  CaffeineCacheManagerCreator caffeineCacheManagerCreator(
      CaffeineCacheToolkitProperties properties) {
    log.trace("[TOOLKIT-CACHE-CAFFEINE] Caffeine Cache Manager Creator");
    return new CaffeineCacheManagerCreator(properties);
  }

  @Bean
  @ConditionalOnCaffeineCacheType
  CaffeineNamedCacheManager caffeineNamedCacheManager(
      CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
    log.trace("[TOOLKIT-CACHE-CAFFEINE] Caffeine Named Cache Manager");
    return caffeineCacheManagerCreator.getInstance();
  }
}
