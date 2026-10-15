package tutorials4j.service.uaa.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.util.SaResult;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.feature.upms.model.DefaultScopes;
import tutorials4j.feature.upms.model.UserInfo;
import tutorials4j.feature.upms.service.UserService;
import tutorials4j.toolkit.core.exception.UnauthorizedException;
import tutorials4j.toolkit.core.model.UserModel;
import tutorials4j.toolkit.satoken.util.SaTokenUtils;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountUaaController {
  private final UserService userService;

  @RequestMapping("/codes")
  public Set<String> codes() {
    return SaTokenUtils.getGrantedAuthorities();
  }

  @RequestMapping("userinfo")
  public UserInfo userinfo() {
    // 获取 Access-Token 对应的账号id
    String accessToken = SaOAuth2Manager.getDataResolver().readAccessToken(SaHolder.getRequest());
    Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);

    UserModel userModel = userService.findUserModelByUserName(loginId.toString());
    UserInfo userInfo = new UserInfo();
    userInfo.setSub(userModel.getUserId());

    var accessTokenModel = SaOAuth2Util.getAccessToken(accessToken);
    if (accessTokenModel.getScopes().contains(DefaultScopes.profile.name())) {
      userInfo.fillByProfile(userModel);
    }

    if (accessTokenModel.getScopes().contains(DefaultScopes.email.name())) {
      userInfo.setEmail(userModel.getEmail());
    }

    if (accessTokenModel.getScopes().contains(DefaultScopes.phone.name())) {
      userInfo.setPhoneNumber(userModel.getPhoneNumber());
    }

    return userInfo;
  }

  @RequestMapping("redirect-uri-and-code")
  public SaResult getRedirectUriAndCode() {
    // 获取变量
    var req = SaHolder.getRequest();
    var cfg = SaOAuth2Manager.getServerConfig();
    var dataGenerate = SaOAuth2Manager.getDataGenerate();
    var oauth2Template = SaOAuth2Manager.getTemplate();
    var responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);

    // 1、先判断是否开启了指定的授权模式
    SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

    // 2、如果尚未登录, 则先去登录
    var loginId = SaOAuth2Manager.getStpLogic().getLoginId("");
    if (StringUtils.isBlank(loginId)) {
      throw new UnauthorizedException();
    }

    // 3、构建请求 Model
    var ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

    // 4、开发者自定义的授权前置检查
    SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(ra.loginId, ra.clientId);

    // 5、校验：重定向域名是否合法
    oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

    // 6、校验：此次申请的Scope，该Client是否已经签约
    oauth2Template.checkContractScope(ra.clientId, ra.scopes);

    // 7、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
    var isNeedCarefulConfirm =
        oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
    if (isNeedCarefulConfirm) {
      var cm = oauth2Template.checkClientModel(ra.clientId);
      if (!cm.getIsAutoConfirm()) {
        // code=411，需要用户手动确认授权
        return SaResult.get(411, "need confirm", null);
      }
    }

    // 8、判断授权类型，重定向到不同地址
    // 		如果是 授权码式，则：开始重定向授权，下放code
    if (SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
      var codeModel = dataGenerate.generateCode(ra);
      var redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
      return SaResult.ok().set("redirect_uri", redirectUri);
    }

    // 		如果是 隐藏式，则：开始重定向授权，下放 token
    if (SaOAuth2Consts.ResponseType.token.equals(ra.responseType)) {
      var at = dataGenerate.generateAccessToken(ra, false, null);
      var redirectUri =
          dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
      return SaResult.ok().set("redirect_uri", redirectUri);
    }

    // 默认返回
    throw new SaOAuth2Exception("无效 response_type: " + ra.responseType)
        .setCode(SaOAuth2ErrorCode.CODE_30125);
  }
}
