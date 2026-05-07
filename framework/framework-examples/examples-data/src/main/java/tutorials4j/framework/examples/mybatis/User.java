package tutorials4j.framework.examples.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 用戶
 *
 * @author Yun Jiao
 */
@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @JsonIgnore  // 返回前端时忽略
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email;

    @Positive(message = "年龄必须为正数")
    private Integer age;

    @JsonIgnore
    private String secretKey;

}