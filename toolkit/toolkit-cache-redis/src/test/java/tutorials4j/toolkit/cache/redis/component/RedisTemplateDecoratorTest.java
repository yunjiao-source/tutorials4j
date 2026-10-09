package tutorials4j.toolkit.cache.redis.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * {@link RedisTemplateDecorator} 单元测试：静态单例与模板持有。
 *
 * @author Yun Jiao
 */
@SuppressWarnings("unchecked")
class RedisTemplateDecoratorTest {
  private final RedisTemplateDecorator decorator = RedisTemplateDecorator.instance;

  @AfterEach
  void tearDown() {
    decorator.setRedisTemplate(null);
    decorator.setStringRedisTemplate(null);
  }

  @Test
  @DisplayName("setter 与静态访问器暴露同一实例")
  void shouldExposeTemplatesThroughStaticAccessors() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    RedisTemplate<Object, Object> redisTemplate = mock(RedisTemplate.class);

    decorator.setStringRedisTemplate(stringRedisTemplate);
    decorator.setRedisTemplate(redisTemplate);

    assertThat(decorator.getStringRedisTemplate()).isSameAs(stringRedisTemplate);
    assertThat(decorator.getRedisTemplate()).isSameAs(redisTemplate);
    assertThat(RedisTemplateDecorator.stringRedisTemplate()).isSameAs(stringRedisTemplate);
    assertThat(RedisTemplateDecorator.redisTemplate()).isSameAs(redisTemplate);
  }

  @Test
  @DisplayName("初始化前静态访问器返回 null（由自动配置负责装配）")
  void shouldReturnNullBeforeInitialization() {
    decorator.setRedisTemplate(null);
    decorator.setStringRedisTemplate(null);

    assertThat(RedisTemplateDecorator.redisTemplate()).isNull();
    assertThat(RedisTemplateDecorator.stringRedisTemplate()).isNull();
  }

  @Test
  @DisplayName("instance 为进程内单例")
  void shouldBeSingleton() {
    assertThat(RedisTemplateDecorator.instance).isSameAs(RedisTemplateDecorator.instance);
  }
}
