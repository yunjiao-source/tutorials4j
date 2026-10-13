package tutorials4j.microservice.oauth.controller;

import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.oauth.event.UserSessionEvent;
import tutorials4j.feature.oauth.event.UserSessionEvent.SessionType;
import tutorials4j.microservice.oauth.component.LoginEventCreatorComponent;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.core.util.SpringUtils;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RestController
@RequestMapping("openapi")
@RequiredArgsConstructor
public class OAuthOpenapiController {
  private final LoginEventCreatorComponent loginEventCreatorComponent;

  @RequestMapping("token")
  public Object token() {
    return SaOAuth2ServerProcessor.instance.token();
  }

  @RequestMapping("authorize")
  public Object authorize() {
    return SaOAuth2ServerProcessor.instance.authorize();
  }

  @RequestMapping("refresh")
  public Object refresh() {
    return SaOAuth2ServerProcessor.instance.refresh();
  }

  @RequestMapping("revoke")
  public Object revoke() {
    return SaOAuth2ServerProcessor.instance.revoke();
  }

  @RequestMapping("client-token")
  public Object clientToken() {
    return SaOAuth2ServerProcessor.instance.clientToken();
  }

  @RequestMapping("doLogin")
  public Object doLogin() {
    return SaOAuth2ServerProcessor.instance.doLogin();
  }

  @RequestMapping("doConfirm")
  public Object doConfirm() {
    return SaOAuth2ServerProcessor.instance.doConfirm();
  }

  @RequestMapping("logout")
  public void logout() {
    UserModel userModel = SaTokenUtils.getUserFromSession();
    StpUtil.logout();

    UserSessionEvent userSessionEvent = loginEventCreatorComponent.create(userModel);
    userSessionEvent.setType(SessionType.logout);
    SpringUtils.getApplicationContext().publishEvent(userSessionEvent);
  }
}
