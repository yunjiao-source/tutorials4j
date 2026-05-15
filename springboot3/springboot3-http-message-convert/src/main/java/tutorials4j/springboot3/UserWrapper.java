package tutorials4j.springboot3;

import lombok.Data;
import tutorials4j.springboot3.convert.User;
import tutorials4j.springboot3.convert.UserFormat;

/**
 * 用户包装
 *
 * @author Yun Jiao
 */
@Data
public class UserWrapper {
  @UserFormat private User user;
  private Integer age;
}
