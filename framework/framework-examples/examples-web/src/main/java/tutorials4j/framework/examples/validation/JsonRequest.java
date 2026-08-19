package tutorials4j.framework.examples.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * JSON 请求体校验请求
 *
 * <p>演示基于 {@code @RequestBody} 的 JSON 请求体校验（触发 {@code MethodArgumentNotValidException}）。
 *
 * @author Yun Jiao
 */
@Data
public class JsonRequest {
  @NotBlank(message = "用户名不能为空")
  @Size(min = 3, max = 20, message = "用户名长度必须在3~20之间")
  private String username;

  @NotBlank(message = "密码不能为空")
  @Size(min = 6, message = "密码长度至少6位")
  private String password;
}
