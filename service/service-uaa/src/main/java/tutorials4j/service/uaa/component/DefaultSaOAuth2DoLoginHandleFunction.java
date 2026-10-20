package tutorials4j.service.uaa.component;

import static tutorials4j.toolkit.core.constant.ToolkitConsts.SESSION_NAME_USER_INFO;

import cn.dev33.satoken.oauth2.function.SaOAuth2DoLoginHandleFunction;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.core.event.local.AuthEvent;
import tutorials4j.feature.core.model.AuthStatus;
import tutorials4j.feature.upms.component.AuthComponent;
import tutorials4j.feature.upms.service.UserService;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.core.util.SpringUtils;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
@RequiredArgsConstructor
public class DefaultSaOAuth2DoLoginHandleFunction implements SaOAuth2DoLoginHandleFunction {

  private final UserService userService;
  private final AuthComponent authComponent;
  private final LoginEventCreatorComponent loginEventCreatorComponent;

  @Override
  public Object apply(String username, String password) {
    UserModel userModel = userService.findUserModelByUserName(username);
    AuthEvent authEvent = loginEventCreatorComponent.create(userModel);

    try {
      authComponent.authenticate(userModel, password);
      authEvent.setStatus(AuthStatus.success);
      SpringUtils.getApplicationContext().publishEvent(authEvent);
    } catch (Exception e) {
      authEvent.setStatus(AuthStatus.fail);
      SpringUtils.getApplicationContext().publishEvent(authEvent);
      throw e;
    }

    boolean rememberMe = SaTokenUtils.extractRememberMe();
    StpUtil.login(userModel.getUsername(), rememberMe);

    StpUtil.getSession().set(SESSION_NAME_USER_INFO, userModel);
    return SaResult.ok("登录成功");
  }
}
