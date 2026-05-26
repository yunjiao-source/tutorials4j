package tutorials4j.springboot3.web.messageconvert;

import lombok.Data;
import tutorials4j.springboot3.web.messageconvert.convert.User;
import tutorials4j.springboot3.web.messageconvert.convert.UserFormat;

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
