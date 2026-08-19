package tutorials4j.framework.data.hibernate.domain;

import com.google.common.base.Objects;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import tutorials4j.framework.common.core.entity.IdEntity;
import tutorials4j.framework.data.hibernate.generator.SnowflakeIdGenerator;

/**
 * 使用雪花算法生成主键的基础实体类，提供 Long 类型主键以及基于主键的 equals/hashCode 实现。
 *
 * @author Yun Jiao
 */
@MappedSuperclass
public class BaseIdEntity implements IdEntity<Long> {
  /** 实体主键，由雪花算法生成。 */
  @Id @SnowflakeIdGenerator private Long id;

  /** 获取主键。 */
  @Override
  public Long getId() {
    return id;
  }

  /** 设置主键。 */
  @Override
  public void setId(Long id) {
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
    BaseIdEntity that = (BaseIdEntity) o;
    return Objects.equal(id, that.id);
  }

  /** 基于主键计算哈希值。 */
  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
