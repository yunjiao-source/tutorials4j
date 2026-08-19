package tutorials4j.framework.examples.hibernate.secondlevelcache;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import tutorials4j.framework.data.hibernate.domain.BaseEntity;

/**
 * 员工实体，用于演示 Hibernate 二级缓存。
 *
 * <p>映射到 {@code data_employees} 表，并配置了读写并发的二级缓存区域 {@code employeeCache}，使该实体的查询结果可以被二级缓存复用。
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(name = "data_employees")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "employeeCache")
public class Employee extends BaseEntity {

  /** 员工姓名，不允许为空 */
  @Column(nullable = false)
  private String name;

  /** 员工所属部门 */
  private String department;
}
