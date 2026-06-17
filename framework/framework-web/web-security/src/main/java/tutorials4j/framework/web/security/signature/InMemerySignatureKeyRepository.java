package tutorials4j.framework.web.security.signature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tutorials4j.framework.web.security.properties.SignatureWebProperties;

/**
 * 基于内存 Map 的简单签名密钥仓库实现。
 *
 * <p>适用于测试或静态密钥场景，生产环境建议使用数据库或配置中心实现。
 *
 * @author Yun Jiao
 */
public class InMemerySignatureKeyRepository implements SignatureKeyRepository {
  // key=appKey, value=appSecret
  protected final Map<String, String> cacheMap = new ConcurrentHashMap<>();

  /**
   * 从配置文件获取
   *
   * @param properties
   */
  public InMemerySignatureKeyRepository(SignatureWebProperties properties) {
    cacheMap.putAll(properties.getKeys());
  }

  @Override
  public String getSecretKey(String key) {
    return cacheMap.get(key);
  }
}
