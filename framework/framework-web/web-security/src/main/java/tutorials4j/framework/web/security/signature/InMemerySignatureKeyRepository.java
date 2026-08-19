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
  /** 签名密钥缓存表，key 为 appKey，value 为 appSecret。 */
  protected final Map<String, String> cacheMap = new ConcurrentHashMap<>();

  /**
   * 从配置文件中加载签名密钥对到内存缓存。
   *
   * @param properties 签名相关 Web 配置，包含 appKey 与 appSecret 的映射
   */
  public InMemerySignatureKeyRepository(SignatureWebProperties properties) {
    cacheMap.putAll(properties.getKeys());
  }

  /** {@inheritDoc} */
  @Override
  public String getSecretKey(String key) {
    return cacheMap.get(key);
  }
}
