package tutorials4j.framework.examples.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 表单校验请求
 *
 * <p>演示基于 {@code @ModelAttribute} 表单提交的参数校验（触发 {@code BindException}）。
 *
 * @author Yun Jiao
 */
@Data
public class FormRequest {
  @NotBlank(message = "姓名不能为空")
  @Size(min = 2, max = 10, message = "姓名长度2~10位")
  private String name;

  @NotBlank(message = "邮箱不能为空")
  private String email;
}
