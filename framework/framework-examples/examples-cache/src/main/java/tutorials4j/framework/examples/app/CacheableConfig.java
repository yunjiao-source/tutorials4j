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
 * 组合缓存应用配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("cacheable")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cacheable"})
public class CacheableConfig {

  @Bean
  CaffeineCacheManager caffeineCacheManager(
      CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
    return caffeineCacheManagerCreator.getInstance();
  }

  @Bean
  @Primary
  RedisCacheManager redisCacheManager(RedisCacheManagerCreator redisCacheManagerCreator) {
    return redisCacheManagerCreator.getInstance();
  }
}
