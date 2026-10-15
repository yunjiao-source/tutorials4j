package tutorials4j.service.uaa.component;

import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import lombok.RequiredArgsConstructor;
import tutorials4j.feature.upms.model.DefaultScopes;
import tutorials4j.feature.upms.model.UserInfo;
import tutorials4j.feature.upms.service.UserService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class DefaultSaOAuth2ScopeHandlerInterface implements SaOAuth2ScopeHandlerInterface {
  private final UserService userService;

  @Override
  public String getHandlerScope() {
    return DefaultScopes.profile.name();
  }

  @Override
  public void workAccessToken(AccessTokenModel at) {
    var loginId = at.getLoginId();
    var userModel = userService.findByUsername(loginId.toString());
    var userInfo = new UserInfo();
    userInfo.fillByProfile(userModel);

    at.extraData.put("profile", userInfo);
  }

  @Override
  public void workClientToken(ClientTokenModel ct) {}

  @Override
  public boolean refreshAccessTokenIsWork() {
    return true;
  }
}
