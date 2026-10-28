package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class UserCreateDTO extends UserUpdateDTO {
  @NotBlank(message = "账号名是必须的")
  private String username;

  private String password;

  private YesNoEnum reserved;
}
