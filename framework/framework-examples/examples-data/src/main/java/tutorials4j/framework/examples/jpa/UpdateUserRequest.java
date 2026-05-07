package tutorials4j.framework.examples.jpa;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 请求DTO类
 *
 * @author Yun Jiao
 */
@Data
public class UpdateUserRequest {
    private String name;
    private String password;
    @Email(message = "邮箱格式不正确")
    private String email;
    @Positive(message = "年龄必须为正数")
    private Integer age;
}
