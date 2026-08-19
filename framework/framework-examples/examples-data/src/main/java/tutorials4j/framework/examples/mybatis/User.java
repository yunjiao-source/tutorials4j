package tutorials4j.framework.examples.mybatis;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Date;
import lombok.Data;
import tutorials4j.framework.examples.SexEnum;

/**
 * 用户实体，对应 MyBatis-Plus 数据表 {@code t_user}。
 *
 * @author Yun Jiao
 */
@Data
@TableName("t_user")
public class User {
  /** 主键 ID，由 MyBatis-Plus 自动分配 */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 姓名，不能为空 */
  @NotBlank(message = "姓名不能为空")
  private String name;

  /** 密码，返回前端时忽略 */
  @JsonIgnore // 返回前端时忽略
  private String password;

  /** 邮箱，不能为空且格式必须合法 */
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式错误")
  private String email;

  /** 年龄，必须为正数 */
  @Positive(message = "年龄必须为正数")
  private Integer age;

  /** 密钥，返回前端时忽略 */
  @JsonIgnore private String secretKey;

  /** 性别 */
  private SexEnum sex;

  /** 创建时间，插入时自动填充 */
  @TableField(fill = FieldFill.INSERT)
  private Date createdDate;

  /** 最后修改时间，更新时自动填充 */
  @TableField(fill = FieldFill.UPDATE)
  private Date lastModifiedDate;
}
