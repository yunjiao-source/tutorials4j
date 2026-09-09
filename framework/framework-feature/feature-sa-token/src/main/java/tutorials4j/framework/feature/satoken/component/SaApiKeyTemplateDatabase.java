package tutorials4j.framework.feature.satoken.component;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.framework.feature.satoken.model.ApiKeyEntity;
import tutorials4j.framework.feature.satoken.service.ApiKeyRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SaApiKeyTemplateDatabase extends SaApiKeyTemplate {
  private final ApiKeyRepository apiKeyRepository;

  public SaApiKeyTemplateDatabase(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  public SaApiKeyTemplateDatabase(ApiKeyRepository apiKeyRepository, String namespace) {
    super(namespace);
    this.apiKeyRepository = apiKeyRepository;
  }

  @Override
  public List<ApiKeyModel> getApiKeyList(Object loginId) {
    Assert.notNull(loginId, "loginId must not be null");
    return apiKeyRepository.findByNamespaceAndLoginId(namespace, loginId.toString()).stream()
        .map(e -> ApiKeyModelConverter.convert(new ApiKeyModel(), e))
        .toList();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveApiKey(ApiKeyModel apiKeyModel) {
    if (apiKeyModel == null) {
      return;
    }

    ApiKeyEntity entity =
        apiKeyRepository
            .findByNamespaceAndApiKeyAndLoginId(
                namespace, apiKeyModel.getApiKey(), apiKeyModel.getLoginId().toString())
            .orElse(new ApiKeyEntity());
    ApiKeyModelConverter.convert(entity, apiKeyModel);
    entity.setNamespace(namespace);
    apiKeyRepository.save(entity);
    super.saveApiKey(apiKeyModel);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteApiKey(String apiKey) {
    apiKeyRepository
        .findByNamespaceAndApiKey(namespace, apiKey)
        .ifPresent(e -> apiKeyRepository.deleteById(e.getId()));
    super.deleteApiKey(apiKey);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteApiKeyByLoginId(Object loginId) {
    Assert.notNull(loginId, "loginId must not be null");
    List<ApiKeyEntity> entities =
        apiKeyRepository.findByNamespaceAndLoginId(namespace, loginId.toString());
    apiKeyRepository.deleteByNamespaceAndLoginId(namespace, loginId.toString());

    for (ApiKeyEntity entity : entities) {
      super.deleteApiKey(entity.getApiKey());
    }
  }
}
