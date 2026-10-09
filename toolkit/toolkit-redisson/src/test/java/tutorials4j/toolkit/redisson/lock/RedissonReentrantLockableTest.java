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
import tutorials4j.toolkit.core.constant.ToolkitConsts;

/**
 * {@link RedissonReentrantLockable} 注解元数据测试。
 *
 * @author Yun Jiao
 */
class RedissonReentrantLockableTest {

  @RedissonReentrantLockable(key = "#id")
  void defaultAnnotated(Long id) {}

  @RedissonReentrantLockable(
      prefix = "biz:",
      key = "#order.id",
      waitTime = 2,
      timeUnit = TimeUnit.SECONDS,
      expireTime = 5)
  void customAnnotated(Object order) {}

  private static RedissonReentrantLockable annotationOf(String methodName, Class<?> parameterType)
      throws NoSuchMethodException {
    return RedissonReentrantLockableTest.class
        .getDeclaredMethod(methodName, parameterType)
        .getAnnotation(RedissonReentrantLockable.class);
  }

  @Test
  @DisplayName("默认前缀 lockable:、默认等待 3000 毫秒、默认不过期（-1）")
  void shouldUseDefaultAttributes() throws NoSuchMethodException {
    RedissonReentrantLockable lockable = annotationOf("defaultAnnotated", Long.class);

    assertThat(lockable).isNotNull();
    assertThat(lockable.prefix())
        .isEqualTo(ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX)
        .isEqualTo("lockable:");
    assertThat(lockable.key()).isEqualTo("#id");
    assertThat(lockable.waitTime()).isEqualTo(3000);
    assertThat(lockable.expireTime()).isEqualTo(-1);
    assertThat(lockable.timeUnit()).isEqualTo(TimeUnit.MILLISECONDS);
  }

  @Test
  @DisplayName("可覆盖前缀、key、等待时间、过期时间与时间单位")
  void shouldOverrideAttributes() throws NoSuchMethodException {
    RedissonReentrantLockable lockable = annotationOf("customAnnotated", Object.class);

    assertThat(lockable.prefix()).isEqualTo("biz:");
    assertThat(lockable.key()).isEqualTo("#order.id");
    assertThat(lockable.waitTime()).isEqualTo(2);
    assertThat(lockable.expireTime()).isEqualTo(5);
    assertThat(lockable.timeUnit()).isEqualTo(TimeUnit.SECONDS);
  }

  @Test
  @DisplayName("仅可用于方法，运行期保留且包含在文档中")
  void shouldDeclareMetaAnnotations() {
    assertThat(List.of(RedissonReentrantLockable.class.getAnnotation(Target.class).value()))
        .containsExactly(ElementType.METHOD);
    assertThat(RedissonReentrantLockable.class.getAnnotation(Retention.class).value())
        .isEqualTo(RetentionPolicy.RUNTIME);
    assertThat(RedissonReentrantLockable.class.getAnnotation(Documented.class)).isNotNull();
  }

  @Test
  @DisplayName("未标注注解的方法读不到注解")
  void shouldNotBePresentOnUnannotatedMethod() throws NoSuchMethodException {
    assertThat(
            RedissonReentrantLockableTest.class
                .getDeclaredMethod("shouldNotBePresentOnUnannotatedMethod")
                .getAnnotation(RedissonReentrantLockable.class))
        .isNull();
  }
}
