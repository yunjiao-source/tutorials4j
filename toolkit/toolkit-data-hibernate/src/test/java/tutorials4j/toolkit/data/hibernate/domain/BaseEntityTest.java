package tutorials4j.toolkit.data.hibernate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.lang.reflect.Field;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tutorials4j.toolkit.data.domain.AuditingEntity;
import tutorials4j.toolkit.data.domain.Entity;
import tutorials4j.toolkit.data.domain.VersionEntity;

/**
 * {@link BaseEntity} 单元测试：乐观锁、审计字段默认值与 JPA 注解映射。
 *
 * @author YunJiao Yang
 */
class BaseEntityTest {

  static class TestEntity extends BaseEntity<Long> {}

  private static Field field(String name) throws NoSuchFieldException {
    return BaseEntity.class.getDeclaredField(name);
  }

  @Test
  @DisplayName("继承 BaseIdEntity 并实现 VersionEntity、AuditingEntity")
  void shouldImplementEntityContracts() {
    TestEntity entity = new TestEntity();

    assertThat(entity)
        .isInstanceOf(BaseIdEntity.class)
        .isInstanceOf(VersionEntity.class)
        .isInstanceOf(AuditingEntity.class)
        .isInstanceOf(Entity.class);
  }

  @Test
  @DisplayName("类为 @MappedSuperclass，并注册 AuditingEntityListener")
  void shouldRegisterAuditingEntityListener() {
    assertThat(BaseEntity.class.getAnnotation(MappedSuperclass.class)).isNotNull();

    EntityListeners listeners = BaseEntity.class.getAnnotation(EntityListeners.class);
    assertThat(listeners).isNotNull();
    assertThat(listeners.value()).containsExactly(AuditingEntityListener.class);
  }

  @Test
  @DisplayName("审计时间字段默认初始化为当前时间，其余字段为 null")
  void shouldInitializeAuditFields() {
    Instant before = Instant.now();

    TestEntity entity = new TestEntity();

    assertThat(entity.getCreatedDate())
        .isNotNull()
        .isBetween(before.minusSeconds(1), Instant.now().plusSeconds(1));
    assertThat(entity.getLastModifiedDate())
        .isNotNull()
        .isBetween(before.minusSeconds(1), Instant.now().plusSeconds(1));
    assertThat(entity.getVersion()).isNull();
    assertThat(entity.getCreatedBy()).isNull();
    assertThat(entity.getLastModifiedBy()).isNull();
    assertThat(entity.getId()).isNull();
  }

  @Test
  @DisplayName("版本号与审计字段可读写")
  void shouldRoundTripVersionAndAuditFields() {
    TestEntity entity = new TestEntity();
    Instant createdDate = Instant.parse("2024-01-01T00:00:00Z");
    Instant lastModifiedDate = Instant.parse("2024-06-01T12:30:00Z");

    entity.setVersion(3);
    entity.setCreatedBy("creator");
    entity.setCreatedDate(createdDate);
    entity.setLastModifiedBy("modifier");
    entity.setLastModifiedDate(lastModifiedDate);

    assertThat(entity.getVersion()).isEqualTo(3);
    assertThat(entity.getCreatedBy()).isEqualTo("creator");
    assertThat(entity.getCreatedDate()).isEqualTo(createdDate);
    assertThat(entity.getLastModifiedBy()).isEqualTo("modifier");
    assertThat(entity.getLastModifiedDate()).isEqualTo(lastModifiedDate);
  }

  @Test
  @DisplayName("版本字段标注 @Version 用于乐观锁")
  void shouldAnnotateVersionField() throws NoSuchFieldException {
    assertThat(field("version").getAnnotation(Version.class)).isNotNull();
  }

  @Test
  @DisplayName("审计字段标注 Spring Data 审计注解")
  void shouldAnnotateAuditingFields() throws NoSuchFieldException {
    assertThat(field("createDate").getAnnotation(CreatedDate.class)).isNotNull();
    assertThat(field("lastModifiedDate").getAnnotation(LastModifiedDate.class)).isNotNull();
    assertThat(field("createBy").getAnnotation(CreatedBy.class)).isNotNull();
    assertThat(field("lastModifiedBy").getAnnotation(LastModifiedBy.class)).isNotNull();
  }

  @Test
  @DisplayName("创建时间不可更新，审计人字段长度为 36")
  void shouldDeclareColumnConstraints() throws NoSuchFieldException {
    Column createDate = field("createDate").getAnnotation(Column.class);
    assertThat(createDate).isNotNull();
    assertThat(createDate.updatable()).isFalse();

    assertThat(field("lastModifiedDate").getAnnotation(Column.class)).isNotNull();

    assertThat(field("createBy").getAnnotation(Column.class).length()).isEqualTo(36);
    assertThat(field("lastModifiedBy").getAnnotation(Column.class).length()).isEqualTo(36);
  }
}
