package tutorials4j.framework.examples.app;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * 缓存示例应用配置类，启用缓存并注册 Caffeine 与 Redis 两种缓存管理器。
 *
 * <p>仅在 {@code cacheable} Profile 下生效，并扫描缓存示例包 {@code tutorials4j.framework.examples.cacheable}
 * 中的组件。
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("cacheable")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cacheable"})
public class CacheableConfig {

  /**
   * 注册 Caffeine 缓存管理器 Bean。
   *
   * @param caffeineCacheManagerCreator Caffeine 缓存管理器创建器
   * @return CaffeineCacheManager 实例
   */
  @Bean
  CaffeineCacheManager caffeineCacheManager(
      CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
    return caffeineCacheManagerCreator.getInstance();
  }

  /**
   * 注册 Redis 缓存管理器 Bean（标注 {@link Primary}，作为默认缓存管理器）。
   *
   * @param redisCacheManagerCreator Redis 缓存管理器创建器
   * @return RedisCacheManager 实例
   */
  @Bean
  @Primary
  RedisCacheManager redisCacheManager(RedisCacheManagerCreator redisCacheManagerCreator) {
    return redisCacheManagerCreator.getInstance();
  }
}
