package tutorials4j.framework.data.hibernate.domain;

import com.google.common.base.Objects;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import tutorials4j.framework.common.core.entity.IdEntity;
import tutorials4j.framework.data.hibernate.generator.SnowflakeIdGenerator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@MappedSuperclass
public class BaseIdEntity implements IdEntity<Long> {
  @Id @SnowflakeIdGenerator private Long id;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseIdEntity that = (BaseIdEntity) o;
    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
