package tutorials4j.toolkit.redisson.lock;

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
 * {@link RedissonBlockLockable} 注解元数据测试。
 *
 * @author YunJiao Yang
 */
class RedissonBlockLockableTest {

  @RedissonBlockLockable(key = "#id")
  void defaultAnnotated(Long id) {}

  @RedissonBlockLockable(
      prefix = "biz:",
      key = "#order.id",
      expireTime = 5,
      timeUnit = TimeUnit.SECONDS)
  void customAnnotated(Object order) {}

  private static RedissonBlockLockable annotationOf(String methodName, Class<?> parameterType)
      throws NoSuchMethodException {
    return RedissonBlockLockableTest.class
        .getDeclaredMethod(methodName, parameterType)
        .getAnnotation(RedissonBlockLockable.class);
  }

  @Test
  @DisplayName("默认前缀为 lockable:，默认不过期（-1）")
  void shouldUseDefaultAttributes() throws NoSuchMethodException {
    RedissonBlockLockable lockable = annotationOf("defaultAnnotated", Long.class);

    assertThat(lockable).isNotNull();
    assertThat(lockable.prefix()).isEqualTo("");
    assertThat(lockable.key()).isEqualTo("#id");
    assertThat(lockable.expireTime()).isEqualTo(-1);
    assertThat(lockable.timeUnit()).isEqualTo(TimeUnit.MILLISECONDS);
  }

  @Test
  @DisplayName("可覆盖前缀、key、过期时间与时间单位")
  void shouldOverrideAttributes() throws NoSuchMethodException {
    RedissonBlockLockable lockable = annotationOf("customAnnotated", Object.class);

    assertThat(lockable.prefix()).isEqualTo("biz:");
    assertThat(lockable.key()).isEqualTo("#order.id");
    assertThat(lockable.expireTime()).isEqualTo(5);
    assertThat(lockable.timeUnit()).isEqualTo(TimeUnit.SECONDS);
  }

  @Test
  @DisplayName("仅可用于方法，运行期保留且包含在文档中")
  void shouldDeclareMetaAnnotations() {
    assertThat(List.of(RedissonBlockLockable.class.getAnnotation(Target.class).value()))
        .containsExactly(ElementType.METHOD);
    assertThat(RedissonBlockLockable.class.getAnnotation(Retention.class).value())
        .isEqualTo(RetentionPolicy.RUNTIME);
    assertThat(RedissonBlockLockable.class.getAnnotation(Documented.class)).isNotNull();
  }

  @Test
  @DisplayName("未标注注解的方法读不到注解")
  void shouldNotBePresentOnUnannotatedMethod() throws NoSuchMethodException {
    assertThat(
            RedissonBlockLockableTest.class
                .getDeclaredMethod("shouldNotBePresentOnUnannotatedMethod")
                .getAnnotation(RedissonBlockLockable.class))
        .isNull();
  }
}
