package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.granttype.handler.PasswordGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.model.PasswordAuthResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.service.UserService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DefaultPasswordGrantTypeHandler extends PasswordGrantTypeHandler {
  private final UserService userService;

  @Override
  public PasswordAuthResult loginByUsernamePassword(String username, String password) {
    var entity = userService.authenticate(username, password);

    return new PasswordAuthResult(entity.getUsername());
  }
}
