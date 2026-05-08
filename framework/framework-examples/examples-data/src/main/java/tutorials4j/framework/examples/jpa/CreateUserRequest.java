package tutorials4j.framework.examples.jpa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tutorials4j.framework.examples.SexEnum;

/**
 * 请求DTO类
 *
 * @author Yun Jiao
 */
@Data
public class CreateUserRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Positive(message = "年龄必须为正数")
    private Integer age;

    private SexEnum sex;
}
