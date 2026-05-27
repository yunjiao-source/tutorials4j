package tutorials4j.springboot3.integration.jasperreports;

import lombok.Data;

/**
 * 用户
 *
 * @author yangyunjiao
 */
@Data
public class User {
  private String id;
  private String name;
  private Integer age;
  private String email;
  private String address;

  public User(String id, String name, Integer age, String email, String address) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.email = email;
    this.address = address;
  }
}
