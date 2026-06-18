package tutorials4j.framework.cache.caffeine.autoconfigure;

import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.caffeine.CaffeineUtils;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;

/**
 * Spring配置类，用于装配Caffeine缓存相关的Bean。
 *
 * @author Yun Jiao
 * @see Caffeine
 * @see NamedCacheProperties
 * @see CaffeineCacheManagerCreator
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CaffeineCacheConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE-CAFFEINE] Cache Caffeine Configuration");
  }

  /**
   * 创建并配置一个{@link Caffeine}实例。
   *
   * @param properties Caffeine缓存配置属性
   * @return 配置好的Caffeine实例
   */
  @Bean
  @ConditionalOnMissingBean
  Caffeine<Object, Object> defaultCaffeine(NamedCacheProperties properties) {
    log.trace("[CACHE-CAFFEINE] Default Caffeine");

    Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
    CaffeineUtils.copyOption(caffeine, properties.getDefaults());
    return caffeine;
  }

  /**
   * 创建{@link CaffeineCacheManagerCreator} Bean，用于生成Caffeine缓存管理器。
   *
   * <p>若上下文中已存在{@link CaffeineCacheManagerCreator}类型的Bean，则不会重复创建。
   *
   * @param caffeine 配置好的Caffeine实例
   * @param properties Caffeine缓存配置属性
   * @return 缓存管理器创建器实例
   */
  @Bean
  @ConditionalOnMissingBean
  CaffeineCacheManagerCreator caffeineCacheManagerCreator(
      Caffeine<Object, Object> caffeine, NamedCacheProperties properties) {
    log.trace("[CACHE-CAFFEINE] Caffeine Cache Manager Creator");

    return new CaffeineCacheManagerCreator(properties, caffeine);
  }
}
