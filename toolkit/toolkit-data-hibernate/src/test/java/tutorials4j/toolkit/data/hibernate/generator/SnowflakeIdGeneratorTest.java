package tutorials4j.toolkit.data.hibernate.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.hibernate.annotations.IdGeneratorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link SnowflakeIdGenerator} 注解元数据测试。
 *
 * @author YunJiao Yang
 */
class SnowflakeIdGeneratorTest {

  @Test
  @DisplayName("通过 @IdGeneratorType 绑定 SnowflakeIdentifierGenerator")
  void shouldBindSnowflakeIdentifierGenerator() {
    IdGeneratorType idGeneratorType =
        SnowflakeIdGenerator.class.getAnnotation(IdGeneratorType.class);

    assertThat(idGeneratorType).isNotNull();
    assertThat(idGeneratorType.value()).isEqualTo(SnowflakeIdentifierGenerator.class);
  }

  @Test
  @DisplayName("保留策略为 RUNTIME")
  void shouldBeRetainedAtRuntime() {
    assertThat(SnowflakeIdGenerator.class.getAnnotation(Retention.class).value())
        .isEqualTo(RetentionPolicy.RUNTIME);
  }

  @Test
  @DisplayName("可用于字段与方法，不可用于类型")
  void shouldTargetFieldAndMethod() {
    Target target = SnowflakeIdGenerator.class.getAnnotation(Target.class);

    assertThat(List.of(target.value()))
        .containsExactlyInAnyOrder(ElementType.FIELD, ElementType.METHOD)
        .doesNotContain(ElementType.TYPE);
  }

  @Test
  @DisplayName("可标注在字段与方法上并被正确读取")
  void shouldBeReadableFromAnnotatedMembers() throws NoSuchFieldException, NoSuchMethodException {
    assertThat(SampleEntity.class.getDeclaredField("id").getAnnotation(SnowflakeIdGenerator.class))
        .isNotNull();
    assertThat(
            SampleEntity.class.getDeclaredMethod("getId").getAnnotation(SnowflakeIdGenerator.class))
        .isNotNull();
  }

  static class SampleEntity {
    @SnowflakeIdGenerator private Long id;

    @SnowflakeIdGenerator
    public Long getId() {
      return id;
    }
  }
}
