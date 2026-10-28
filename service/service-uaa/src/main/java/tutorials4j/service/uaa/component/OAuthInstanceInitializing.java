package tutorials4j.service.uaa.component;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import tutorials4j.feature.upms.UpmsFeatureManager;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
@RequiredArgsConstructor
public class OAuthInstanceInitializing implements SmartInitializingSingleton {
  private final DefaultSaOAuth2ConfirmViewFunction defaultSaOAuth2ConfirmViewFunction;
  private final DefaultSaOAuth2NotLoginViewFunction defaultSaOAuth2NotLoginViewFunction;
  private final DefaultSaOAuth2DoLoginHandleFunction defaultSaOAuth2DoLoginHandleFunction;

  private void initUpmsFeatureManager() {
    UpmsFeatureManager.getInstance().setClientEntityDefaultValue =
        (clientEntity) -> {
          if (StringUtils.isBlank(clientEntity.getClientId())) {
            clientEntity.setClientId(IdUtil.fastUUID());
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
            clientEntity.setIsNewRefresh(YesNoEnum.valueOf(config.getIsNewRefresh()));
          }
        };
  }

  private void initSaOAuth2Server() {
    // 未登录的视图
    SaOAuth2Strategy.instance.notLoginView = defaultSaOAuth2NotLoginViewFunction;

    // 登录处理函数
    SaOAuth2Strategy.instance.doLoginHandle = defaultSaOAuth2DoLoginHandleFunction;

    // 授权确认视图
    SaOAuth2Strategy.instance.confirmView = defaultSaOAuth2ConfirmViewFunction;
  }

  @Override
  public void afterSingletonsInstantiated() {
    this.initUpmsFeatureManager();
    this.initSaOAuth2Server();
  }
}
