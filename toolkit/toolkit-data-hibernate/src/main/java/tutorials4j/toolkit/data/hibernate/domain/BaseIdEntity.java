package tutorials4j.toolkit.data.hibernate.domain;

import com.google.common.base.Objects;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import java.io.Serializable;
import lombok.ToString;
import tutorials4j.toolkit.data.domain.IdEntity;
import tutorials4j.toolkit.data.hibernate.generator.SnowflakeIdGenerator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@ToString
@MappedSuperclass
public class BaseIdEntity<ID extends Serializable> implements IdEntity<ID> {
  @Id @SnowflakeIdGenerator private ID id;

  @Override
  public ID getId() {
    return id;
  }

  @Override
  public void setId(ID id) {
    this.id = id;
  }

  /** 基于主键判断两个实体是否相等，仅比较类型与主键值。 */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseIdEntity<?> that = (BaseIdEntity<?>) o;
    return id != null && Objects.equal(id, that.id);
  }

  /** 基于主键计算哈希值。 */
  @Override
  public int hashCode() {
    return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
  }

  @PrePersist
  protected void prePersist() {}

  @PreUpdate
  protected void preUpdate() {}

  @PreRemove
  protected void preRemove() {}
}
