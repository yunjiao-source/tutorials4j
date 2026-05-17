package tutorials4j.framework.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.redis.PrefixKeyStringRedisSerializer;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;
import tutorials4j.framework.cache.redis.customizer.NamedCacheManagerCustomizer;
import tutorials4j.framework.cache.redis.customizer.NamedRedisCacheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.customizer.ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.lock.RedisLockService;
import tutorials4j.framework.cache.redis.lock.RedisLockableAspect;
import tutorials4j.framework.cache.redis.util.RedisBitmapUtils;
import tutorials4j.framework.common.core.content.SpelMethodBasedExpressionEvaluator;

/**
 * 命名缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedisCacheConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CACHE-REDIS] Cache Redis Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer
      jsonSerializerRedisCacheManagerBuilderCustomizer() {
    log.debug("[CACHE-REDIS] Json Serializer Value Redis Cache Manager Builder Customizerr");
    return new ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer();
  }

  @Bean
  @ConditionalOnMissingBean
  NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(
      NamedCacheProperties properties) {
    log.debug("[CACHE-REDIS] Named Redis Cache Manager Builder Customizer");
    return new NamedRedisCacheManagerBuilderCustomizer(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
    log.debug("[CACHE-REDIS] Named Redis Cache Manager Customizer");
    return new NamedCacheManagerCustomizer();
  }

  @Bean
  @ConditionalOnMissingBean
  RedisCacheManagerCreator redisCacheManagerCreator(
      NamedCacheProperties properties,
      RedisConnectionFactory factory,
      ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
      ObjectProvider<CacheManagerCustomizer<RedisCacheManager>> cacheManagerCustomizers) {
    log.debug("[CACHE-REDIS] Redis Cache Manager Creator");
    return new RedisCacheManagerCreator(
        properties,
        factory,
        redisCacheManagerBuilderCustomizers.orderedStream().collect(Collectors.toList()),
        cacheManagerCustomizers.orderedStream().collect(Collectors.toList()));
  }

  @Bean
  @ConditionalOnMissingBean
  RedisBitmapUtils redisBitmapUtils(
      @Qualifier(value = "stringRedisTemplate") StringRedisTemplate stringRedisTemplate) {
    log.debug("[CACHE-REDIS] Redis Bitmap Utils");
    RedisBitmapUtils utils = new RedisBitmapUtils();
    utils.setStringRedisTemplate(stringRedisTemplate);
    return utils;
  }

  @Bean
  @ConditionalOnMissingBean
  RedisLockService redisLockService(
      @Qualifier(value = "stringRedisTemplate") StringRedisTemplate stringRedisTemplate) {
    log.debug("[CACHE-REDIS] Redis Lock Service");
    return new RedisLockService(stringRedisTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  RedisLockableAspect redisLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedisLockService redisLockService) {
    log.debug("[CACHE-REDIS] Redis Lockable Aspect");
    return new RedisLockableAspect(spelMethodBasedExpressionEvaluator, redisLockService);
  }

  @ConditionalOnBean({StringRedisTemplate.class, RedisTemplate.class})
  @Configuration(proxyBeanMethods = false)
  public static class InnerConfiguration {
    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired private RedisTemplate<Object, Object> redisTemplate;

    @PostConstruct
    public void postConstruct() {
      log.debug("[CACHE-REDIS] String Redis Template");
      PrefixKeyStringRedisSerializer serializer = new PrefixKeyStringRedisSerializer();
      stringRedisTemplate.setKeySerializer(serializer);
      stringRedisTemplate.setHashKeySerializer(serializer);

      log.debug("[CACHE-REDIS] Redis Template");
      redisTemplate.setKeySerializer(serializer);
      redisTemplate.setHashKeySerializer(serializer);
    }
  }
}
