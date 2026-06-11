package tutorials4j.framework.examples.mybatis;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tutorials4j.framework.examples.SexEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class UserUpdateDTO {
  private String name;
  private String password;
  private SexEnum sex;
  @Email private String email;
  @Positive private Integer age;
}
