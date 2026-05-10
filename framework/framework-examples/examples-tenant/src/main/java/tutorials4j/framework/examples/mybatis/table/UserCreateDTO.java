package tutorials4j.framework.examples.mybatis.table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class UserCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;
    @Positive
    private Integer age;

}
