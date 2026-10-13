package tutorials4j.toolkit.cache.redis.lock;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link RedisLockable} 注解元数据测试。
 *
 * @author YunJiao Yang
 */
class RedisLockableTest {

  @RedisLockable(key = "#id")
  void defaultAnnotated(Long id) {}

  @RedisLockable(prefix = "biz:", key = "#order.id", expireTime = 5, timeUnit = TimeUnit.SECONDS)
  void customAnnotated(Object order) {}

  private static RedisLockable annotationOf(String methodName, Class<?> parameterType)
      throws NoSuchMethodException {
    return RedisLockableTest.class
        .getDeclaredMethod(methodName, parameterType)
        .getAnnotation(RedisLockable.class);
  }

  @Test
  @DisplayName("默认前缀与默认过期配置")
  void shouldUseDefaultAttributes() throws NoSuchMethodException {
    RedisLockable redisLockable = annotationOf("defaultAnnotated", Long.class);

    assertThat(redisLockable).isNotNull();
    assertThat(redisLockable.prefix()).isEqualTo("");
    assertThat(redisLockable.key()).isEqualTo("#id");
    assertThat(redisLockable.expireTime()).isEqualTo(-1);
    assertThat(redisLockable.timeUnit()).isEqualTo(TimeUnit.MILLISECONDS);
  }

  @Test
  @DisplayName("可覆盖前缀、key、过期时间与时间单位")
  void shouldOverrideAttributes() throws NoSuchMethodException {
    RedisLockable redisLockable = annotationOf("customAnnotated", Object.class);

    assertThat(redisLockable.prefix()).isEqualTo("biz:");
    assertThat(redisLockable.key()).isEqualTo("#order.id");
    assertThat(redisLockable.expireTime()).isEqualTo(5);
    assertThat(redisLockable.timeUnit()).isEqualTo(TimeUnit.SECONDS);
  }

  @Test
  @DisplayName("仅可用于方法，运行期保留且包含在文档中")
  void shouldDeclareMetaAnnotations() {
    assertThat(List.of(RedisLockable.class.getAnnotation(Target.class).value()))
        .containsExactly(ElementType.METHOD);
    assertThat(RedisLockable.class.getAnnotation(Retention.class).value())
        .isEqualTo(RetentionPolicy.RUNTIME);
    assertThat(RedisLockable.class.getAnnotation(Documented.class)).isNotNull();
  }

  @Test
  @DisplayName("未标注注解的方法读不到注解")
  void shouldNotBePresentOnUnannotatedMethod() throws NoSuchMethodException {
    assertThat(
            RedisLockableTest.class
                .getDeclaredMethod("shouldNotBePresentOnUnannotatedMethod")
                .getAnnotation(RedisLockable.class))
        .isNull();
  }
}
