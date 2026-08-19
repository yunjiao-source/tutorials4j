package tutorials4j.framework.examples.validation;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册表单
 *
 * <p>包含用户名、邮箱、密码、确认密码、年龄、手机号、性别等字段及对应的校验规则， 配合 {@code @PasswordMatch} 注解实现确认密码一致性校验。
 *
 * @author Yun Jiao
 */
@Data
public class UserForm {

  @NotBlank(message = "用户名不能为空")
  @Length(min = 3, max = 20, message = "用户名长度必须在3-20位之间")
  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户名只能包含字母和数字")
  private String username;

  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  @NotBlank(message = "密码不能为空")
  @Length(min = 6, max = 20, message = "密码长度必须在6-20位之间")
  private String password;

  // 自定义校验：确认密码与密码一致（通过@PasswordMatch注解实现，见下文）
  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;

  @NotNull(message = "年龄不能为空")
  @Min(value = 18, message = "年龄必须大于等于18")
  @Max(value = 60, message = "年龄必须小于等于60")
  private Integer age;

  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确，需为1开头的11位数字")
  private String phone; // 选填

  @NotNull(message = "请选择性别")
  private Integer gender; // 1-男，2-女
}
