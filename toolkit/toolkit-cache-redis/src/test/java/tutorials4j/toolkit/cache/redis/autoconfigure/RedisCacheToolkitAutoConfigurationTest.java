package tutorials4j.toolkit.cache.redis.autoconfigure;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.LinkedHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.ClassUtils;
import tutorials4j.toolkit.cache.redis.component.RedisCacheManagerCreator;
import tutorials4j.toolkit.cache.redis.component.RedisTemplateDecorator;
import tutorials4j.toolkit.cache.redis.lock.RedisLockService;
import tutorials4j.toolkit.cache.redis.lock.RedisLockableAspect;
import tutorials4j.toolkit.cache.redis.script.RedisScriptExecutor;
import tutorials4j.toolkit.cache.redis.support.ToolkitKeySerializer;
import tutorials4j.toolkit.core.constant.PropertyConsts;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;
import tutorials4j.toolkit.json.component.JsonTypeWhiteListLoader;

/**
 * {@link RedisCacheToolkitAutoConfiguration} 自动装配测试。
 *
 * @author YunJiao Yang
 */
class RedisCacheToolkitAutoConfigurationTest {
  private static final String PREFIX = PropertyConsts.PROPERTY_TOOLKIT_CACHE_REDIS;

  private final RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(RedisCacheToolkitAutoConfiguration.class))
          .withBean(RedisConnectionFactory.class, () -> connectionFactory)
          .withBean(JsonTypeWhiteListLoader.class, JsonTypeWhiteListLoader::new)
          .withBean(
              SpelMethodBasedExpressionEvaluator.class, SpelMethodBasedExpressionEvaluator::new);

  @AfterEach
  void tearDown() {
    RedisTemplateDecorator.instance.setRedisTemplate(null);
    RedisTemplateDecorator.instance.setStringRedisTemplate(null);
  }

  /** 白名单外的普通类型，用于验证序列化器的类型白名单校验。 */
  static class Probe {
    private String name = "probe";

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @Test
  @DisplayName("装配缓存管理器创建器、序列化器、模板装饰器、脚本执行器、锁服务与切面")
  void shouldRegisterBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(RedisCacheToolkitAutoConfiguration.class);
          assertThat(context).hasSingleBean(RedisCacheToolkitProperties.class);
          assertThat(context).hasSingleBean(RedisCacheManagerCreator.class);
          assertThat(context).hasSingleBean(GenericJacksonJsonRedisSerializer.class);
          assertThat(context).hasSingleBean(RedisTemplateDecorator.class);
          assertThat(context).hasSingleBean(RedisScriptExecutor.class);
          assertThat(context).hasSingleBean(RedisLockService.class);
          assertThat(context).hasSingleBean(RedisLockableAspect.class);
        });
  }

  @Test
  @DisplayName("锁服务 Bean 为静态单例")
  void shouldExposeLockServiceSingleton() {
    contextRunner.run(
        context ->
            assertThat(context.getBean(RedisLockService.class))
                .isSameAs(RedisLockService.instance));
  }

  @Test
  @DisplayName("RedisTemplate 使用统一 key 前缀的序列化器，并绑定连接工厂")
  void shouldConfigureRedisTemplates() {
    contextRunner.run(
        context -> {
          RedisTemplateDecorator decorator = context.getBean(RedisTemplateDecorator.class);

          assertThat(decorator.getStringRedisTemplate()).isNotNull();
          assertThat(decorator.getRedisTemplate()).isNotNull();
          assertThat(decorator.getStringRedisTemplate().getKeySerializer())
              .isInstanceOf(ToolkitKeySerializer.class);
          assertThat(decorator.getRedisTemplate().getKeySerializer())
              .isInstanceOf(ToolkitKeySerializer.class);
          assertThat(decorator.getRedisTemplate().getHashKeySerializer())
              .isInstanceOf(ToolkitKeySerializer.class);
          assertThat(decorator.getStringRedisTemplate().getConnectionFactory())
              .isSameAs(connectionFactory);

          // 静态访问器指向同一实例
          assertThat(RedisTemplateDecorator.stringRedisTemplate())
              .isSameAs(decorator.getStringRedisTemplate());
          assertThat(RedisTemplateDecorator.redisTemplate()).isSameAs(decorator.getRedisTemplate());

          // 默认模板前缀
          ToolkitKeySerializer keySerializer =
              (ToolkitKeySerializer)
                  RedisTemplateDecorator.stringRedisTemplate().getKeySerializer();
          assertThat(keySerializer.serialize("k")).isEqualTo("template:k".getBytes(UTF_8));
        });
  }

  @Test
  @DisplayName("模板 key 前缀可通过配置调整")
  void shouldUseConfiguredTemplateKeyPrefix() {
    contextRunner
        .withPropertyValues(PREFIX + ".template-key-prefix=cache:")
        .run(
            context -> {
              ToolkitKeySerializer keySerializer =
                  (ToolkitKeySerializer)
                      RedisTemplateDecorator.stringRedisTemplate().getKeySerializer();

              assertThat(keySerializer.serialize("k")).isEqualTo("cache:k".getBytes(UTF_8));
            });
  }

  @Test
  @DisplayName("缓存管理器创建器可按配置创建命名缓存")
  void shouldCreateNamedCacheThroughCreator() {
    contextRunner
        .withPropertyValues(
            PREFIX + ".caches.users.time-to-live=1m", PREFIX + ".caches.users.key-prefix=user:")
        .run(
            context -> {
              RedisCacheManagerCreator creator = context.getBean(RedisCacheManagerCreator.class);

              assertThat(creator.getInstance()).isNotNull();
              assertThat(creator.getInstance().getCache("users")).isNotNull();
            });
  }

  @Test
  @DisplayName("JSON 序列化器写入类型信息，且白名单类型可正常往返")
  void shouldRoundTripWhitelistedType() {
    contextRunner.run(
        context -> {
          GenericJacksonJsonRedisSerializer serializer =
              context.getBean(GenericJacksonJsonRedisSerializer.class);
          LinkedHashMap<String, String> value = new LinkedHashMap<>();
          value.put("name", "alice");

          byte[] bytes = serializer.serialize(value);

          assertThat(new String(bytes, UTF_8))
              .contains("@class")
              .contains("java.util.LinkedHashMap");
          assertThat(serializer.deserialize(bytes)).isEqualTo(value);
        });
  }

  @Test
  @DisplayName("现状记录：白名单只约束反序列化——非白名单类型可被写出，但读取该载荷会被拒绝")
  void shouldEnforceWhiteListOnDeserializeOnly() {
    contextRunner.run(
        context -> {
          GenericJacksonJsonRedisSerializer serializer =
              context.getBean(GenericJacksonJsonRedisSerializer.class);

          byte[] bytes = serializer.serialize(new Probe());
          assertThat(new String(bytes, UTF_8)).contains("@class").contains(Probe.class.getName());

          assertThatThrownBy(() -> serializer.deserialize(bytes))
              .isInstanceOf(SerializationException.class);
        });
  }

  @Test
  @DisplayName("JSON 反序列化拒绝白名单外的 @class 类型")
  void shouldRejectNonWhitelistedTypeOnDeserialize() {
    contextRunner.run(
        context -> {
          GenericJacksonJsonRedisSerializer serializer =
              context.getBean(GenericJacksonJsonRedisSerializer.class);
          byte[] payload = "{\"@class\":\"java.util.HashMap\",\"name\":\"alice\"}".getBytes(UTF_8);

          assertThatThrownBy(() -> serializer.deserialize(payload))
              .isInstanceOf(SerializationException.class);
        });
  }

  @Test
  @DisplayName("类标注 @AutoConfiguration")
  void shouldBeAnnotatedAsAutoConfiguration() {
    assertThat(RedisCacheToolkitAutoConfiguration.class.getAnnotation(AutoConfiguration.class))
        .isNotNull();
  }

  @Test
  @DisplayName("已登记在 META-INF 自动配置清单中")
  void shouldBeRegisteredInAutoConfigurationImports() {
    ClassLoader classLoader = getClass().getClassLoader();
    String expectedName = RedisCacheToolkitAutoConfiguration.class.getName();

    assertThat(ImportCandidates.load(AutoConfiguration.class, classLoader))
        .anyMatch(className -> className.equals(expectedName));
    assertThat(ClassUtils.resolveClassName(expectedName, classLoader)).isNotNull();
  }
}
