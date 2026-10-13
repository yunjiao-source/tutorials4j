package tutorials4j.microservice.oauth.component;

import static tutorials4j.toolkit.core.constant.ToolkitConsts.SESSION_NAME_USER_INFO;

import cn.dev33.satoken.oauth2.granttype.handler.PasswordGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.model.PasswordAuthResult;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.event.UserSessionEvent;
import tutorials4j.feature.oauth.event.UserSessionEvent.SessionType;
import tutorials4j.feature.oauth.service.UserService;
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
public class DefaultPasswordGrantTypeHandler extends PasswordGrantTypeHandler {
  private final UserService userService;
  private final LoginEventCreatorComponent loginEventCreatorComponent;

  @Override
  public PasswordAuthResult loginByUsernamePassword(String username, String password) {
    UserModel userModel = userService.findUserModelByUserName(username);
    UserSessionEvent userSessionEvent = loginEventCreatorComponent.create(userModel);

    try {
      userService.authenticate(userModel, password);
      userSessionEvent.setType(SessionType.loginSuccess);
      SpringUtils.getApplicationContext().publishEvent(userSessionEvent);
    } catch (Exception e) {
      userSessionEvent.setType(SessionType.loginFailure);
      SpringUtils.getApplicationContext().publishEvent(userSessionEvent);
      throw e;
    }

    boolean rememberMe = SaTokenUtils.extractRememberMe();
    StpUtil.login(userModel.getUsername(), rememberMe);

    StpUtil.getSession().set(SESSION_NAME_USER_INFO, userModel);
    return new PasswordAuthResult(userModel.getUsername());
  }
}
