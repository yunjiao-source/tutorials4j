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

/**
 * 用户实体，对应数据表 data_users。
 *
 * <p>用户与订单为一对多关系，性别字段通过属性转换器以编码形式存储。
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(name = "data_users")
public class User extends BaseStatusEntity {
  /** 用户名，唯一且非空 */
  @Column(unique = true, nullable = false)
  private String username;

  /** 邮箱 */
  private String email;

  /** 年龄 */
  private Integer age;

  /** 用户的订单列表 */
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Order> orders = new ArrayList<>();

  /** 性别 */
  @Convert(converter = SexEnumAttributeConverter.class)
  private SexEnum sex;
}
