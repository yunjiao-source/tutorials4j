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
import cn.dev33.satoken.util.SaResult;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.toolkit.core.exception.UnauthorizedException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("oauth2")
public class OAuth2Controller {

  @RequestMapping("userinfo")
  public SaResult userinfo() {
    // 获取 Access-Token 对应的账号id
    String accessToken = SaOAuth2Manager.getDataResolver().readAccessToken(SaHolder.getRequest());
    Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);

    // 校验 Access-Token 是否具有权限: userinfo
    SaOAuth2Util.checkAccessTokenScope(accessToken, "userinfo");

    // 模拟账号信息 （真实环境需要查询数据库获取信息）
    Map<String, Object> map = new LinkedHashMap<>();
    // map.put("userId", loginId);  一般原则下，oauth2-server 不能把 userId 返回给 oauth2-client
    map.put("nickname", "林小林");
    map.put("avatar", "http://xxx.com/1.jpg");
    map.put("age", "18");
    map.put("sex", "男");
    map.put("address", "山东省 青岛市 城阳区");
    return SaResult.ok().setMap(map);
  }

  @RequestMapping("*")
  public Object request() {
    return SaOAuth2ServerProcessor.instance.dister();
  }

  @RequestMapping("redirect-uri-and-code")
  public SaResult getRedirectUriAndCode() {
    // 获取变量
    SaRequest req = SaHolder.getRequest();
    SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
    SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();
    SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
    String responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);

    // 1、先判断是否开启了指定的授权模式
    SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

    // 2、如果尚未登录, 则先去登录
    String loginId = SaOAuth2Manager.getStpLogic().getLoginId("");
    if (StringUtils.isBlank(loginId)) {
      throw new UnauthorizedException();
    }

    // 3、构建请求 Model
    RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

    // 4、开发者自定义的授权前置检查
    SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(ra.loginId, ra.clientId);

    // 5、校验：重定向域名是否合法
    oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

    // 6、校验：此次申请的Scope，该Client是否已经签约
    oauth2Template.checkContractScope(ra.clientId, ra.scopes);

    // 7、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
    boolean isNeedCarefulConfirm =
        oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
    if (isNeedCarefulConfirm) {
      SaClientModel cm = oauth2Template.checkClientModel(ra.clientId);
      if (!cm.getIsAutoConfirm()) {
        // code=411，需要用户手动确认授权
        return SaResult.get(411, "need confirm", null);
      }
    }

    // 8、判断授权类型，重定向到不同地址
    // 		如果是 授权码式，则：开始重定向授权，下放code
    if (SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
      CodeModel codeModel = dataGenerate.generateCode(ra);
      String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
      return SaResult.ok().set("redirect_uri", redirectUri);
    }

    // 		如果是 隐藏式，则：开始重定向授权，下放 token
    if (SaOAuth2Consts.ResponseType.token.equals(ra.responseType)) {
      AccessTokenModel at = dataGenerate.generateAccessToken(ra, false, null);
      String redirectUri =
          dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
      return SaResult.ok().set("redirect_uri", redirectUri);
    }

    // 默认返回
    throw new SaOAuth2Exception("无效 response_type: " + ra.responseType)
        .setCode(SaOAuth2ErrorCode.CODE_30125);
  }
}
