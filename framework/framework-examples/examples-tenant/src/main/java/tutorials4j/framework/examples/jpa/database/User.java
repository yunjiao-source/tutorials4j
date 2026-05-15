package tutorials4j.framework.examples.jpa.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import tutorials4j.framework.data.hibernate.SnowflakeIDGenerator;

/**
 * 用戶
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "t_user")
public class User {
  @Id
  // @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SnowflakeIDGenerator
  private Long id;

  private String name;
  private String password;
  private String email;
  private Integer age;
  private String secretKey;

  public static User of(String name, String email) {
    User user = new User();
    user.name = name;
    user.email = email;
    return user;
  }
}
