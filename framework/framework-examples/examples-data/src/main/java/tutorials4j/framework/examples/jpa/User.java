package tutorials4j.framework.examples.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import tutorials4j.framework.data.hibernate.SnowflakeIDGenerator;
import tutorials4j.framework.examples.SexEnum;

/**
 * 用戶
 *
 * @author Yun Jiao
 */
@Data
@Entity
@Table(name = "t_user")
public class User {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @SnowflakeIDGenerator
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @JsonIgnore
    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Convert(converter = SexEnumAttributeConverter.class)
    private SexEnum sex;

    @Positive(message = "年龄必须为正数")
    private Integer age;

    @JsonIgnore
    private String secretKey;  // 密钥字段，不返回前端
}
