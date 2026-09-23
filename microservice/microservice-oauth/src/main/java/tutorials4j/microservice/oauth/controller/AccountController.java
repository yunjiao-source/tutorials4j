package tutorials4j.microservice.oauth.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.oauth.entity.UserEntity;
import tutorials4j.feature.oauth.model.DefaultScopes;
import tutorials4j.feature.oauth.model.UserInfo;
import tutorials4j.feature.oauth.service.UserService;
import tutorials4j.toolkit.core.exception.UnauthorizedException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountController {

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

  @RequestMapping("/codes")
  public List<String> codes() {
    return StpUtil.getPermissionList();
  }

  @RequestMapping("logout")
  public void logout() {
    StpUtil.logout();
  }

}
