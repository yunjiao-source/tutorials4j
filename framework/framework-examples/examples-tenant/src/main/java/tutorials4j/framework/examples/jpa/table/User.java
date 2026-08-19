package tutorials4j.framework.examples.jpa.table;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.tenant.hibernate.TenantEntity;

/**
 * 用户实体。
 *
 * <p>多租户示例中使用的 JPA 实体，映射数据库表 {@code t_user_tenant}，继承 {@link TenantEntity} 以支持租户数据隔离。
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_user_tenant")
public class User extends TenantEntity {
  /** 主键，使用序列生成策略。 */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  /** 用户名。 */
  private String name;

  /** 密码。 */
  private String password;

  /** 邮箱。 */
  private String email;

  /** 年龄。 */
  private Integer age;

  /** 密钥。 */
  private String secretKey;

  /**
   * 创建用户实例。
   *
   * @param name 用户名
   * @param email 邮箱
   * @return 用户实例
   */
  public static User of(String name, String email) {
    User user = new User();
    user.name = name;
    user.email = email;
    return user;
  }
}
