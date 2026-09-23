package tutorials4j.feature.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class UserCreateModel extends UserUpdateModel {
  @NotBlank(message = "账号名是必须的")
  private String username;

  @NotBlank(message = "密码是必须的")
  private String password;
}
