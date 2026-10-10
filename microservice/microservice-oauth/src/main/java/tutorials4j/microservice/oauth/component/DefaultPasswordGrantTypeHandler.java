package tutorials4j.microservice.oauth.component;

import static tutorials4j.toolkit.core.constant.ToolkitConsts.SESSION_NAME_USER_INFO;

import cn.dev33.satoken.oauth2.granttype.handler.PasswordGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.model.PasswordAuthResult;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.service.UserService;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

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
    UserModel userModel = userService.findUserModelByUserName(username);
    userService.authenticate(userModel, password);

    boolean rememberMe = SaTokenUtils.extractRememberMe();
    StpUtil.login(userModel.getUsername(), rememberMe);

    StpUtil.getSession().set(SESSION_NAME_USER_INFO, userModel);
    return new PasswordAuthResult(userModel.getUsername());
  }
}
