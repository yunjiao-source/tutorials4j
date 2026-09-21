package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.model.OidcUserOpenIdCreateModel;
import tutorials4j.feature.oauth.model.OidcUserUnionIdCreateModel;
import tutorials4j.feature.oauth.service.ClientService;
import tutorials4j.feature.oauth.service.OidcUserIdentityService;
import tutorials4j.microservice.oauth.util.ConverterUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DatabaseSaOAuth2DataLoader implements SaOAuth2DataLoader {
  private final ClientService clientService;
  private final OidcUserIdentityService oidcUserIdentityService;

  @Override
  public SaClientModel getClientModel(String clientId) {
    return clientService
        .getClientRepository()
        .findByClientId(clientId)
        .map(e -> ConverterUtils.getInstance().convertClientEntity2Model.apply(e))
        .orElse(null);
  }

  @Override
  public String getOpenid(String clientId, Object loginId) {
    var oidcUserIdentityEntity =
        oidcUserIdentityService
            .getOidcUserIdentityRepository()
            .findByUserIdAndClientId(loginId.toString(), clientId)
            .orElseGet(
                () -> {
                  String prefix = IdUtil.fastSimpleUUID();
                  String openId = SaSecureUtil.md5(prefix + "_" + clientId + "_" + loginId);
                  OidcUserOpenIdCreateModel model =
                      OidcUserOpenIdCreateModel.builder()
                          .userId(loginId.toString())
                          .clientId(clientId)
                          .prefix(prefix)
                          .openId(openId)
                          .build();
                  return oidcUserIdentityService.createOpenId(model);
                });
    return oidcUserIdentityEntity.getOpenId();
  }

  @Override
  public String getUnionid(String subjectId, Object loginId) {
    var oidcUserIdentityEntity =
        oidcUserIdentityService
            .getOidcUserIdentityRepository()
            .findByUserIdAndSubjectId(loginId.toString(), subjectId)
            .orElseGet(
                () -> {
                  String prefix = IdUtil.fastSimpleUUID();
                  String unionId = SaSecureUtil.md5(prefix + "_" + subjectId + "_" + loginId);
                  OidcUserUnionIdCreateModel model =
                      OidcUserUnionIdCreateModel.builder()
                          .userId(loginId.toString())
                          .subjectId(subjectId)
                          .prefix(prefix)
                          .unionId(unionId)
                          .build();
                  return oidcUserIdentityService.createUnionId(model);
                });
    return oidcUserIdentityEntity.getUnionId();
  }
}
