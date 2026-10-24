package tutorials4j.toolkit.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tutorials4j.toolkit.cache.context.ConditionalOnRedisCacheType;
import tutorials4j.toolkit.cache.redis.component.RedisCacheManagerCreator;
import tutorials4j.toolkit.cache.redis.component.RedisNamedCacheManager;
import tutorials4j.toolkit.cache.redis.component.RedisTemplateDecorator;
import tutorials4j.toolkit.cache.redis.lock.RedisLockService;
import tutorials4j.toolkit.cache.redis.lock.RedisLockableAspect;
import tutorials4j.toolkit.cache.redis.script.RedisScriptExecutor;
import tutorials4j.toolkit.cache.redis.support.RedisNamedCacheConfigurationCustomizer;
import tutorials4j.toolkit.cache.redis.support.RedisNamedCacheManagerCustomizer;
import tutorials4j.toolkit.cache.redis.support.ToolkitKeySerializer;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;
import tutorials4j.toolkit.json.component.ObjectMapperCreator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({
  RedisCacheToolkitProperties.class,
})
public class RedisCacheToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Cache Toolkit Auto Configuration");
  }

  @Bean
  RedisCacheManagerCreator redisCacheManagerCreator(
      RedisCacheToolkitProperties properties,
      RedisConnectionFactory factory,
      ObjectProvider<RedisNamedCacheConfigurationCustomizer>
          redisNamedCacheConfigurationCustomizers,
      ObjectProvider<RedisNamedCacheManagerCustomizer> redisNamedCacheManagerCustomizers,
      GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Cache Manager Creator");
    return new RedisCacheManagerCreator(
        properties,
        factory,
        redisNamedCacheConfigurationCustomizers.orderedStream().toList(),
        redisNamedCacheManagerCustomizers.orderedStream().toList(),
        genericJacksonJsonRedisSerializer);
  }

  @Bean
  public GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer(
      ObjectMapperCreator objectMapperCreator) {
    log.trace("[TOOLKIT-CACHE-REDIS] Generic Jackson Json Redis Serializer");
    return new GenericJacksonJsonRedisSerializer(objectMapperCreator.getInstance());
  }

  @Bean
  RedisTemplateDecorator redisTemplateDecorator(
      RedisConnectionFactory redisConnectionFactory,
      RedisCacheToolkitProperties properties,
      GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Template Decorator");

    ToolkitKeySerializer serializer = new ToolkitKeySerializer(properties.getTemplateKeyPrefix());
    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
    stringRedisTemplate.setDefaultSerializer(serializer);
    stringRedisTemplate.setKeySerializer(serializer);
    stringRedisTemplate.setHashKeySerializer(serializer);

    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setDefaultSerializer(serializer);
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(serializer);
    redisTemplate.setValueSerializer(genericJacksonJsonRedisSerializer);
    redisTemplate.setHashKeySerializer(serializer);
    redisTemplate.setHashValueSerializer(genericJacksonJsonRedisSerializer);

    RedisTemplateDecorator.instance.setRedisTemplate(redisTemplate);
    RedisTemplateDecorator.instance.setStringRedisTemplate(stringRedisTemplate);
    return RedisTemplateDecorator.instance;
  }

  @Bean
  RedisScriptExecutor redisScriptExecutor(RedisTemplateDecorator redisTemplateDecorator) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Script Executor");
    return new RedisScriptExecutor(redisTemplateDecorator);
  }

  @Bean
  RedisLockService redisLockService(RedisScriptExecutor redisScriptExecutor) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Lock Service");
    RedisLockService.instance.setRedisScriptExecutor(redisScriptExecutor);
    return RedisLockService.instance;
  }

  @Bean
  RedisLockableAspect redisLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedisLockService redisLockService) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Lockable Aspect");
    return new RedisLockableAspect(spelMethodBasedExpressionEvaluator, redisLockService);
  }

  @Bean
  @ConditionalOnRedisCacheType
  RedisNamedCacheManager redisNamedCacheManager(RedisCacheManagerCreator redisCacheManagerCreator) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Named Cache Manager");
    return redisCacheManagerCreator.getInstance();
  }
}
