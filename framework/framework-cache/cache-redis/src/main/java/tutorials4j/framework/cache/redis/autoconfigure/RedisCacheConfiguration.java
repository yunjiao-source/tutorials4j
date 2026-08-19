package tutorials4j.framework.cache.redis.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Redis 缓存自动配置类。
 *
 * <p>装配 Redis 缓存相关的核心 Bean：缓存管理器创建器 {@link RedisCacheManagerCreator}、各类缓存管理器定制器、 基于租户 Key 前缀的
 * {@link RedisTemplateDecorator}，以及分布式锁服务 {@link RedisLockService} 与切面 {@link RedisLockableAspect}。
 *
 * @author Yun Jiao
 * @see RedisCacheManagerCreator
 * @see RedisLockService
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RedisCacheConfiguration {
  /** 初始化日志输出。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CACHE-REDIS] Cache Redis Configuration");
  }

  /**
   * 创建值序列化为 JSON 的缓存管理器构建器定制器。
   *
   * @param objectMapper Jackson 对象映射器
   * @return 值 JSON 序列化定制器实例
   */
  @Bean
  @ConditionalOnMissingBean
  ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer
      jsonSerializerRedisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
    log.trace("[CACHE-REDIS] Json Serializer Value Redis Cache Manager Builder Customizerr");
    return new ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer(objectMapper);
  }

  /**
   * 创建命名缓存管理器构建器定制器。
   *
   * @param properties 命名缓存配置属性
   * @return 命名缓存构建器定制器实例
   */
  @Bean
  @ConditionalOnMissingBean
  NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(
      NamedCacheProperties properties) {
    log.trace("[CACHE-REDIS] Named Redis Cache Manager Builder Customizer");
    return new NamedRedisCacheManagerBuilderCustomizer(properties);
  }

  /**
   * 创建命名缓存管理器定制器，用于强制初始化命名缓存。
   *
   * @return 命名缓存管理器定制器实例
   */
  @Bean
  @ConditionalOnMissingBean
  NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
    log.trace("[CACHE-REDIS] Named Redis Cache Manager Customizer");
    return new NamedCacheManagerCustomizer();
  }

  /**
   * 创建 Redis 缓存管理器创建器。
   *
   * @param properties 命名缓存配置属性
   * @param factory Redis 连接工厂
   * @param redisCacheManagerBuilderCustomizers 构建器定制器集合
   * @param cacheManagerCustomizers 缓存管理器定制器集合
   * @return Redis 缓存管理器创建器实例
   */
  @Bean
  @ConditionalOnMissingBean
  RedisCacheManagerCreator redisCacheManagerCreator(
      NamedCacheProperties properties,
      RedisConnectionFactory factory,
      ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
      ObjectProvider<CacheManagerCustomizer<RedisCacheManager>> cacheManagerCustomizers) {
    log.trace("[CACHE-REDIS] Redis Cache Manager Creator");
    return new RedisCacheManagerCreator(
        properties,
        factory,
        redisCacheManagerBuilderCustomizers.orderedStream().collect(Collectors.toList()),
        cacheManagerCustomizers.orderedStream().collect(Collectors.toList()));
  }

  /**
   * 创建 Redis 模板装饰器，配置基于租户 Key 前缀的序列化器并注入单例。
   *
   * @param redisConnectionFactory Redis 连接工厂
   * @param properties 缓存配置属性
   * @return {@link RedisTemplateDecorator} 单例实例
   */
  @Bean
  @ConditionalOnMissingBean
  RedisTemplateDecorator redisTemplateDecorator(
      RedisConnectionFactory redisConnectionFactory, CacheProperties properties) {
    log.trace("[CACHE-REDIS] Redis Template Decorator");

    // 基于租户key的序列化器
    TenantKeySerializer serializer = new TenantKeySerializer(properties.getTemplateCacheName());
    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
    stringRedisTemplate.setKeySerializer(serializer);
    stringRedisTemplate.setHashKeySerializer(serializer);

    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(serializer);
    redisTemplate.setHashKeySerializer(serializer);

    RedisTemplateDecorator.instance.setRedisTemplate(redisTemplate);
    RedisTemplateDecorator.instance.setStringRedisTemplate(stringRedisTemplate);
    return RedisTemplateDecorator.instance;
  }

  /**
   * 创建 Redis 分布式锁服务。
   *
   * @param redisTemplateDecorator Redis 模板装饰器
   * @param properties 锁缓存配置属性
   * @return {@link RedisLockService} 单例实例
   */
  @Bean
  @ConditionalOnMissingBean
  RedisLockService redisLockService(
      RedisTemplateDecorator redisTemplateDecorator, LockCacheProperties properties) {
    log.trace("[CACHE-REDIS] Redis Lock Service");
    RedisLockService.instance.setRedisLockOptions(properties.getRedis());
    RedisLockService.instance.setRedisTemplateDecorator(redisTemplateDecorator);
    return RedisLockService.instance;
  }

  /**
   * 创建 {@link RedisLockable} 注解的 AOP 切面。
   *
   * @param spelMethodBasedExpressionEvaluator SpEL 表达式求值器
   * @param redisLockService Redis 分布式锁服务
   * @return 分布式锁切面实例
   */
  @Bean
  @ConditionalOnMissingBean
  RedisLockableAspect redisLockableAspect(
      SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator,
      RedisLockService redisLockService) {
    log.trace("[CACHE-REDIS] Redis Lockable Aspect");
    return new RedisLockableAspect(spelMethodBasedExpressionEvaluator, redisLockService);
  }
}
