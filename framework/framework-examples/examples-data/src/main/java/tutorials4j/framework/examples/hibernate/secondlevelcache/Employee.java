package tutorials4j.framework.examples.hibernate.secondlevelcache;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import tutorials4j.framework.data.hibernate.domain.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "data_employees")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "employeeCache")
public class Employee extends BaseEntity {

  @Column(nullable = false)
  private String name;

  private String department;
}
