package tutorials4j.framework.feature.satoken.component;

import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import tutorials4j.framework.feature.satoken.service.ApiKeyRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SaApiKeyDataLoaderDatabase implements SaApiKeyDataLoader {
  private final ApiKeyRepository apiKeyRepository;

  @Override
  public Boolean getIsRecordIndex() {
    return false;
  }

  @Override
  public ApiKeyModel getApiKeyModelFromDatabase(String namespace, String apiKey) {
    Assert.hasText(namespace, "namespace must not be null or empty");
    Assert.hasText(apiKey, "apiKey must not be null or empty");
    return apiKeyRepository
        .findByNamespaceAndApiKey(namespace, apiKey)
        .map(e -> ApiKeyModelConverter.convert(new ApiKeyModel(), e))
        .orElse(null);
  }
}
