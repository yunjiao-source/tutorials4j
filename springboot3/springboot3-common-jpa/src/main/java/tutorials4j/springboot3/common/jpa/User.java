package tutorials4j.springboot3.common.jpa;

import jakarta.persistence.*;
import lombok.Data;

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
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
