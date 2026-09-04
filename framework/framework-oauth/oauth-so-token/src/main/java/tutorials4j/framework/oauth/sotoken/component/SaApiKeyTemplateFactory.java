package tutorials4j.framework.oauth.sotoken.component;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SaApiKeyTemplateFactory {
  private ConcurrentMap<String, SaApiKeyTemplate> cacheMap = new ConcurrentHashMap<>();

  public SaApiKeyTemplate getDefaultTemplate() {
    return cacheMap.computeIfAbsent(
        "DEFAULT", (namespace) -> SaApiKeyManager.getSaApiKeyTemplate());
  }

  public SaApiKeyTemplate getTemplate(String namespace) {
    return cacheMap.computeIfAbsent(namespace, this::createTemplate);
  }

  private SaApiKeyTemplate createTemplate(String namespace) {
    return new SaApiKeyTemplate(namespace);
  }
}
