package tutorials4j.microservice.oauth.config;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import tutorials4j.feature.oauth.OAuthFeatureManager;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Configuration(proxyBeanMethods = false)
public class OAuthFeatureManagerConfiguration {
  @PostConstruct
  void init() {
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
}
