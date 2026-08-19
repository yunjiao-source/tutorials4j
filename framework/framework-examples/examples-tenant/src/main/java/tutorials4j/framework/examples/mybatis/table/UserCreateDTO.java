package tutorials4j.framework.examples.mybatis.table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 创建用户请求 DTO。
 *
 * <p>用于接收前端提交的新增用户信息，其中姓名、密码与邮箱为必填项。
 *
 * @author Yun Jiao
 */
@Data
public class UserCreateDTO {
  /** 姓名 */
  @NotBlank private String name;

  /** 密码 */
  @NotBlank private String password;

  /** 邮箱 */
  @NotBlank @Email private String email;

  /** 年龄 */
  @Positive private Integer age;
}
