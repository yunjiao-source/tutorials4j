package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.granttype.handler.PasswordGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.model.PasswordAuthResult;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.model.UserModel;
import tutorials4j.feature.oauth.service.UserService;
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
    var entity = userService.authenticate(username, password);

    boolean rememberMe = SaTokenUtils.extractRememberMe();
    StpUtil.login(entity.getUsername(), rememberMe);

    UserModel model = new UserModel();
    BeanUtils.copyProperties(entity, model);
    StpUtil.getSession().set("userinfo", model);
    return new PasswordAuthResult(entity.getUsername());
  }
}
