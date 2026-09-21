package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import lombok.RequiredArgsConstructor;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.feature.oauth.model.DefaultScopes;
import tutorials4j.feature.oauth.model.UserInfo;
import tutorials4j.feature.oauth.service.UserService;

/**
 * TODO
 *
 * @author Yun Jiao
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
    Object loginId = at.getLoginId();
    UserEntity entity = userService.findByUsername(loginId.toString());
    UserInfo userInfo = new UserInfo();
    userInfo.fillByProfile(entity);

    at.extraData.put("profile", userInfo);
  }

  @Override
  public void workClientToken(ClientTokenModel ct) {}

  @Override
  public boolean refreshAccessTokenIsWork() {
    return true;
  }
}
