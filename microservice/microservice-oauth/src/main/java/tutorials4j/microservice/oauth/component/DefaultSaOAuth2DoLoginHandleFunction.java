package tutorials4j.microservice.oauth.component;

import static tutorials4j.toolkit.core.constant.ToolkitConsts.SESSION_NAME_USER_INFO;

import cn.dev33.satoken.oauth2.function.SaOAuth2DoLoginHandleFunction;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.service.UserService;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DefaultSaOAuth2DoLoginHandleFunction implements SaOAuth2DoLoginHandleFunction {

  private final UserService userService;

  @Override
  public Object apply(String username, String password) {
    var userModel = userService.findUserModelByUserName(username);
    userService.authenticate(userModel, password);

    boolean rememberMe = SaTokenUtils.extractRememberMe();
    StpUtil.login(userModel.getUsername(), rememberMe);

    StpUtil.getSession().set(SESSION_NAME_USER_INFO, userModel);
    return SaResult.ok("登录成功");
  }
}
