package tutorials4j.framework.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import tutorials4j.framework.cache.core.properties.CacheProperties;
import tutorials4j.framework.cache.core.properties.LockCacheProperties;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisTemplateDecorator;
import tutorials4j.framework.cache.redis.TenantKeySerializer;
import tutorials4j.framework.cache.redis.customizer.NamedCacheManagerCustomizer;
import tutorials4j.framework.cache.redis.customizer.NamedRedisCacheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.customizer.ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.lock.RedisLockService;
import tutorials4j.framework.cache.redis.lock.RedisLockableAspect;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

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
  RedisTemplateDecorator redisTemplateDecorator(
      CacheProperties properties,
      StringRedisTemplate stringRedisTemplate,
      RedisTemplate<Object, Object> redisTemplate) {
    log.debug("[CACHE-REDIS] Redis Template Decorator");

    // 基于租户key的序列化器
    TenantKeySerializer serializer = new TenantKeySerializer(properties.getTemplateCacheName());
    stringRedisTemplate.setKeySerializer(serializer);
    stringRedisTemplate.setHashKeySerializer(serializer);

    redisTemplate.setKeySerializer(serializer);
    redisTemplate.setHashKeySerializer(serializer);

    RedisTemplateDecorator.instance.setRedisTemplate(redisTemplate);
    RedisTemplateDecorator.instance.setStringRedisTemplate(stringRedisTemplate);
    return RedisTemplateDecorator.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  RedisLockService redisLockService(
      RedisTemplateDecorator redisTemplateDecorator, LockCacheProperties properties) {
    log.debug("[CACHE-REDIS] Redis Lock Service");
    RedisLockService.instance.setRedisLockOptions(properties.getRedis());
    RedisLockService.instance.setRedisTemplateDecorator(redisTemplateDecorator);
    return RedisLockService.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  RedisLockableAspect redisLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedisLockService redisLockService) {
    log.debug("[CACHE-REDIS] Redis Lockable Aspect");
    return new RedisLockableAspect(spelMethodBasedExpressionEvaluator, redisLockService);
  }
}
