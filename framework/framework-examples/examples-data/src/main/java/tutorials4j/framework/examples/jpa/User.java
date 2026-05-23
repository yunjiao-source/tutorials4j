package tutorials4j.framework.examples.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.framework.data.hibernate.domain.BaseStatusEntity;
import tutorials4j.framework.examples.SexEnum;

@Getter
@Setter
@Entity
@Table(name = "data_users")
public class User extends BaseStatusEntity {
  @Column(unique = true, nullable = false)
  private String username;

  private String email;
  private Integer age;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Order> orders = new ArrayList<>();

  @Convert(converter = SexEnumAttributeConverter.class)
  private SexEnum sex;
}
