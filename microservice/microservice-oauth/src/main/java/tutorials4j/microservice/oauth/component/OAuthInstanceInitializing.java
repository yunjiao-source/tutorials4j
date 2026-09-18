package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.IdUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import tutorials4j.feature.oauth.OAuthFeatureManager;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class OAuthInstanceInitializing implements SmartInitializingSingleton {

  private void initOAuthFeatureManager() {
    OAuthFeatureManager.getInstance().setClientEntityDefaultValue =
        (clientEntity) -> {
          if (StringUtils.isBlank(clientEntity.getClientId())) {
            clientEntity.setClientId(IdUtil.fastUUID());
          }
          if (clientEntity.getIsAutoConfirm() == null) {
            clientEntity.setIsAutoConfirm(YesNoEnum.no);
          }
          if (clientEntity.getAllowGrantTypes() == null) {
            clientEntity.setAllowGrantTypes(List.of());
          }
          if (clientEntity.getAllowRedirectUris() == null) {
            clientEntity.setAllowRedirectUris(List.of());
          }
          if (clientEntity.getContractScopes() == null) {
            clientEntity.setContractScopes(List.of());
          }

          SaOAuth2ServerConfig config = SaOAuth2Manager.getServerConfig();
          if (clientEntity.getAccessTokenTimeout() == null) {
            clientEntity.setAccessTokenTimeout(config.getAccessTokenTimeout());
          }
          if (clientEntity.getRefreshTokenTimeout() == null) {
            clientEntity.setRefreshTokenTimeout(config.getRefreshTokenTimeout());
          }
          if (clientEntity.getClientTokenTimeout() == null) {
            clientEntity.setClientTokenTimeout(config.getClientTokenTimeout());
          }
          if (clientEntity.getMaxAccessTokenCount() == null) {
            clientEntity.setMaxAccessTokenCount(config.getMaxAccessTokenCount());
          }
          if (clientEntity.getMaxRefreshTokenCount() == null) {
            clientEntity.setMaxRefreshTokenCount(config.getMaxRefreshTokenCount());
          }
          if (clientEntity.getMaxClientTokenCount() == null) {
            clientEntity.setMaxClientTokenCount(config.getMaxClientTokenCount());
          }
          if (clientEntity.getIsNewRefresh() == null) {
            YesNoEnum isNewRefresh =
                Objects.equals(config.getIsNewRefresh(), Boolean.TRUE)
                    ? YesNoEnum.yes
                    : YesNoEnum.no;
            clientEntity.setIsNewRefresh(isNewRefresh);
          }
        };
  }

  private void initSaOAuth2Server() {
    // 未登录的视图
    SaOAuth2Strategy.instance.notLoginView =
        () -> {
          return new ModelAndView("login.html");
        };

    // 登录处理函数
    SaOAuth2Strategy.instance.doLoginHandle =
        (name, pwd) -> {
          if ("sa".equals(name) && "123456".equals(pwd)) {
            StpUtil.login(10001);
            return SaResult.ok().set("satoken", StpUtil.getTokenValue());
          }
          return SaResult.error("账号名或密码错误");
        };

    // 授权确认视图
    SaOAuth2Strategy.instance.confirmView =
        (clientId, scopes) -> {
          Map<String, Object> map = new HashMap<>();
          map.put("clientId", clientId);
          map.put("scope", scopes);
          return new ModelAndView("confirm.html", map);
        };
  }

  @Override
  public void afterSingletonsInstantiated() {
    this.initOAuthFeatureManager();
    this.initSaOAuth2Server();
  }
}
