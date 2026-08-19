package tutorials4j.framework.examples.mybatis;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tutorials4j.framework.examples.SexEnum;

/**
 * 更新用户请求的 DTO。
 *
 * @author Yun Jiao
 */
@Data
public class UserUpdateDTO {
  /** 姓名 */
  private String name;

  /** 密码（有变更时才加密） */
  private String password;

  /** 性别 */
  private SexEnum sex;

  /** 邮箱，格式必须合法 */
  @Email private String email;

  /** 年龄，必须为正数 */
  @Positive private Integer age;
}
