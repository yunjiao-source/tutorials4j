package tutorials4j.framework.examples.mybatis.table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 更新用户请求 DTO。
 *
 * <p>用于接收前端提交的用户更新信息，所有字段均可选，仅更新传入的字段。
 *
 * @author Yun Jiao
 */
@Data
public class UserUpdateDTO {
  /** 姓名 */
  private String name;

  /** 密码 */
  private String password;

  /** 邮箱 */
  @Email private String email;

  /** 年龄 */
  @Positive private Integer age;
}
