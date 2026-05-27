package tutorials4j.springboot3.web.validation;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 实体
 *
 * @author yangyunjiao
 */
@Data
public class User {
  @Min(value = 10, message = "年龄必须大于10岁")
  private Integer age;
}
