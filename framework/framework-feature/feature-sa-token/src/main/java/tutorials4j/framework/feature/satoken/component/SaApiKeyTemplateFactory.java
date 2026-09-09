package tutorials4j.framework.feature.satoken.component;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.Assert;
import tutorials4j.framework.feature.satoken.service.ApiKeyRepository;
import tutorials4j.framework.oauth.satoken.common.SaTokenOauthConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SaApiKeyTemplateFactory implements SmartInitializingSingleton {
  private final ConcurrentMap<String, SaApiKeyTemplate> cacheMap = new ConcurrentHashMap<>();
  private final ApiKeyRepository apiKeyRepository;

  public SaApiKeyTemplate getDefaultTemplate() {
    return cacheMap.computeIfAbsent(
        SaTokenOauthConsts.DEFAULT_NAMESPACE, (namespace) -> SaApiKeyManager.getSaApiKeyTemplate());
  }

  public SaApiKeyTemplate getTemplate(String namespace) {
    Assert.hasText(namespace, "namespace is not be null or empty");
    return cacheMap.computeIfAbsent(namespace, this::createTemplate);
  }

  private SaApiKeyTemplate createTemplate(String namespace) {
    return new SaApiKeyTemplateDatabase(apiKeyRepository, namespace);
  }

  @Override
  public void afterSingletonsInstantiated() {
    // 管理器使用自定义类

    SaApiKeyTemplateDatabase saApiKeyTemplateDatabase =
        new SaApiKeyTemplateDatabase(apiKeyRepository);
    SaApiKeyDataLoaderDatabase saApiKeyDataLoaderDatabase =
        new SaApiKeyDataLoaderDatabase(apiKeyRepository);
    SaApiKeyManager.setSaApiKeyTemplate(saApiKeyTemplateDatabase);
    SaApiKeyManager.setSaApiKeyDataLoader(saApiKeyDataLoaderDatabase);
  }
}
