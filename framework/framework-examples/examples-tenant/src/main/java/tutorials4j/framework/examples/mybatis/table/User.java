package tutorials4j.framework.examples.mybatis.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 用户实体类。
 *
 * <p>对应数据库中的 {@code t_user_tenant} 表，演示 MyBatis-Plus 表级租户隔离场景下的实体映射。
 *
 * @author Yun Jiao
 */
@Data
@TableName("t_user_tenant")
public class User {
  /** 主键 ID，由 MyBatis-Plus 自动分配 */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 姓名 */
  @NotBlank(message = "姓名不能为空")
  private String name;

  /** 密码 */
  @JsonIgnore // 返回前端时忽略
  private String password;

  /** 邮箱 */
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式错误")
  private String email;

  /** 年龄 */
  @Positive(message = "年龄必须为正数")
  private Integer age;

  /** 密钥 */
  @JsonIgnore private String secretKey;
}
