package tutorials4j.framework.web.core.support;

import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * 基于内存 Map 的简单签名密钥仓库实现。
 *
 * <p>适用于测试或静态密钥场景，生产环境建议使用数据库或配置中心实现。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class SimpleSignatureKeyRepository implements SignatureKeyRepository {
  private final Map<String, String> cacheMap;

  @Override
  public String getSecretKey(String key) {
    return cacheMap.get(key);
  }
}
