package tutorials4j.framework.examples.mybatis;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tutorials4j.framework.examples.SexEnum;

/**
 * 创建用户请求的 DTO。
 *
 * @author Yun Jiao
 */
@Data
public class UserCreateDTO {
  /** 姓名，不能为空 */
  @NotBlank private String name;

  /** 密码，不能为空 */
  @NotBlank private String password;

  /** 邮箱，不能为空且格式必须合法 */
  @NotBlank @Email private String email;

  /** 年龄，必须为正数 */
  @Positive private Integer age;

  /** 性别 */
  private SexEnum sex;
}
