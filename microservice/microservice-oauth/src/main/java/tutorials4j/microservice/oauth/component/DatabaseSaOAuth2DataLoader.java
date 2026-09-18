package tutorials4j.microservice.oauth.component;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tutorials4j.feature.oauth.entity.OpenUserEntity;
import tutorials4j.feature.oauth.model.OpenIdCreateModel;
import tutorials4j.feature.oauth.model.UnionIdCreateModel;
import tutorials4j.feature.oauth.repository.ClientRepository;
import tutorials4j.feature.oauth.repository.OpenUserRepository;
import tutorials4j.feature.oauth.service.OpenUserService;
import tutorials4j.microservice.oauth.util.ConverterUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DatabaseSaOAuth2DataLoader implements SaOAuth2DataLoader {
  private final ClientRepository clientRepository;
  private final OpenUserService openUserService;
  private final OpenUserRepository openUserRepository;

  @Override
  public SaClientModel getClientModel(String clientId) {
    return clientRepository
        .findByClientId(clientId)
        .map(e -> ConverterUtils.getInstance().convertClientEntity2Model.apply(e))
        .orElse(null);
  }

  @Override
  public String getOpenid(String clientId, Object loginId) {
    OpenUserEntity openUserEntity =
        openUserRepository
            .findByUserIdAndClientId(loginId.toString(), clientId)
            .orElseGet(
                () -> {
                  String prefix = IdUtil.fastSimpleUUID();
                  String openId = SaSecureUtil.md5(prefix + "_" + clientId + "_" + loginId);
                  OpenIdCreateModel model =
                      OpenIdCreateModel.builder()
                          .userId(loginId.toString())
                          .clientId(clientId)
                          .prefix(prefix)
                          .openId(openId)
                          .build();
                  return openUserService.createOpenId(model);
                });
    return openUserEntity.getOpenId();
  }

  @Override
  public String getUnionid(String subjectId, Object loginId) {
    OpenUserEntity openUserEntity =
        openUserRepository
            .findByUserIdAndSubjectId(loginId.toString(), subjectId)
            .orElseGet(
                () -> {
                  String prefix = IdUtil.fastSimpleUUID();
                  String unionId = SaSecureUtil.md5(prefix + "_" + subjectId + "_" + loginId);
                  UnionIdCreateModel model =
                      UnionIdCreateModel.builder()
                          .userId(loginId.toString())
                          .subjectId(subjectId)
                          .prefix(prefix)
                          .unionId(unionId)
                          .build();
                  return openUserService.createUnionId(model);
                });
    return openUserEntity.getUnionId();
  }
}
