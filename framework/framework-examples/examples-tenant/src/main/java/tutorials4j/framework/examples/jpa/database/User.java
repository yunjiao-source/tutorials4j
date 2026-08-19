package tutorials4j.framework.examples.jpa.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import tutorials4j.framework.data.hibernate.generator.SnowflakeIdGenerator;

/**
 * 用户实体，映射数据库表 {@code t_user}，用于演示 JPA 持久化示例。
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "t_user")
public class User {
  @Id
  // @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SnowflakeIdGenerator
  private Long id;

  private String name;
  private String password;
  private String email;
  private Integer age;
  private String secretKey;

  /**
   * 创建用户实例的静态工厂方法。
   *
   * @param name 用户名
   * @param email 邮箱
   * @return 新创建的用户实例
   */
  public static User of(String name, String email) {
    User user = new User();
    user.name = name;
    user.email = email;
    return user;
  }
}
