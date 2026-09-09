package tutorials4j.framework.feature.satoken.component;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tutorials4j.framework.feature.satoken.model.ApiKeyCreateModel;
import tutorials4j.framework.feature.satoken.model.ApiKeyUpdateModel;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class ApiKeyComponent {
  private final SaApiKeyTemplateFactory saApiKeyTemplateFactory;

  @Transactional(rollbackFor = Exception.class)
  public ApiKeyModel create(ApiKeyCreateModel model) {
    SaApiKeyTemplate saApiKeyTemplate = saApiKeyTemplateFactory.getTemplate(model.getNamespace());

    ApiKeyModel apiKeyModel = saApiKeyTemplate.createApiKeyModel(model.getLoginId());
    model.fillTo(apiKeyModel);

    // 持久化
    saApiKeyTemplate.saveApiKey(apiKeyModel);
    return apiKeyModel;
  }

  @Transactional(rollbackFor = Exception.class)
  public ApiKeyModel update(ApiKeyUpdateModel model) {
    Assert.notNull(model, "model must not be null");

    SaApiKeyTemplate saApiKeyTemplate = saApiKeyTemplateFactory.getTemplate(model.getNamespace());
    saApiKeyTemplate.checkApiKeyLoginId(model.getApiKey(), model.getLoginId());
    ApiKeyModel apiKeyModel = saApiKeyTemplate.getApiKey(model.getApiKey());
    model.fillTo(apiKeyModel);

    saApiKeyTemplate.saveApiKey(apiKeyModel);
    return apiKeyModel;
  }

  public List<ApiKeyModel> list(String namespace, Object loginId) {
    Assert.notNull(loginId, "loginId must not be null");
    return saApiKeyTemplateFactory.getTemplate(namespace).getApiKeyList(loginId);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(String namespace, Object loginId, String apiKey) {
    Assert.notNull(loginId, "loginId must not be null");
    SaApiKeyTemplate saApiKeyTemplate = saApiKeyTemplateFactory.getTemplate(namespace);
    saApiKeyTemplate.checkApiKeyLoginId(apiKey, loginId);
    saApiKeyTemplate.deleteApiKey(apiKey);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteAll(String namespace, Object loginId) {
    Assert.notNull(loginId, "loginId must not be null");
    SaApiKeyTemplate saApiKeyTemplate = saApiKeyTemplateFactory.getTemplate(namespace);
    saApiKeyTemplate.deleteApiKeyByLoginId(loginId);
  }
}
