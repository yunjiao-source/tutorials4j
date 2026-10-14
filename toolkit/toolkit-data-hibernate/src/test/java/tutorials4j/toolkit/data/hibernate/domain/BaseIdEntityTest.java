package tutorials4j.toolkit.data.hibernate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tutorials4j.toolkit.data.domain.IdEntity;
import tutorials4j.toolkit.data.hibernate.generator.SnowflakeIdGenerator;

/**
 * {@link BaseIdEntity} 单元测试：主键映射与基于主键的相等性语义。
 *
 * @author YunJiao Yang
 */
class BaseIdEntityTest {

  static class TestIdEntity extends BaseIdEntity<Long> {}

  static class OtherIdEntity extends BaseIdEntity<Long> {}

  static class SubTestIdEntity extends TestIdEntity {}

  private static TestIdEntity entity(Long id) {
    TestIdEntity entity = new TestIdEntity();
    entity.setId(id);
    return entity;
  }

  @Test
  @DisplayName("类为 @MappedSuperclass，主键字段标注 @Id 与 @SnowflakeIdGenerator")
  void shouldDeclareMappedIdWithSnowflakeGenerator() throws NoSuchFieldException {
    assertThat(BaseIdEntity.class.getAnnotation(MappedSuperclass.class)).isNotNull();

    assertThat(BaseIdEntity.class.getDeclaredField("id").getAnnotation(Id.class)).isNotNull();
    assertThat(BaseIdEntity.class.getDeclaredField("id").getAnnotation(SnowflakeIdGenerator.class))
        .isNotNull();
  }

  @Test
  @DisplayName("实现 IdEntity，可读写主键")
  void shouldImplementIdEntity() {
    TestIdEntity entity = new TestIdEntity();

    assertThat(entity).isInstanceOf(IdEntity.class);
    assertThat(entity.getId()).isNull();

    entity.setId(100L);

    assertThat(entity.getId()).isEqualTo(100L);
  }

  @Test
  @DisplayName("isNew 以主键是否为 null 判断")
  void shouldReportNewByPrimaryKey() {
    assertThat(entity(null).isNew()).isTrue();
    assertThat(entity(1L).isNew()).isFalse();
  }

  @Test
  @DisplayName("同类型同主键相等，且 hashCode 一致")
  void shouldBeEqualWithSameTypeAndId() {
    TestIdEntity first = entity(1L);
    TestIdEntity second = entity(1L);

    assertThat(first).isEqualTo(second).hasSameHashCodeAs(second);
    assertThat(first).isEqualTo(first);
  }

  @Test
  @DisplayName("主键不同则不相等")
  void shouldNotBeEqualWithDifferentId() {
    assertThat(entity(1L)).isNotEqualTo(entity(2L));
  }

  @Test
  @DisplayName("类型不同即使主键相同也不相等（含子类）")
  void shouldNotBeEqualWithDifferentType() {
    TestIdEntity base = entity(1L);
    OtherIdEntity other = new OtherIdEntity();
    other.setId(1L);

    SubTestIdEntity sub = new SubTestIdEntity();
    sub.setId(1L);

    assertThat(base).isNotEqualTo(other);
    assertThat(base).isNotEqualTo(sub);
  }

  @Test
  @DisplayName("与 null 及其他类型对象不相等")
  void shouldNotBeEqualWithNullOrOtherType() {
    TestIdEntity entity = entity(1L);

    assertThat(entity).isNotEqualTo(null);
    assertThat(entity).isNotEqualTo("1");
  }

  @Test
  @DisplayName("主键为 null 时两个新实体不相等且 hashCode 不一致")
  void shouldHandleNullIdInHashCode() {
    TestIdEntity first = entity(null);
    TestIdEntity second = entity(null);

    assertThat(first).isNotEqualTo(second).doesNotHaveSameHashCodeAs(second);
  }
}
