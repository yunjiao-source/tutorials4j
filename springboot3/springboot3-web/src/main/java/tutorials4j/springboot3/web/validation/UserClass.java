package tutorials4j.springboot3.web.validation;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * 对象包装
 *
 * @author yangyunjiao
 */
@Data
public class UserClass {
  private String className;
  @Valid private User user;
}
