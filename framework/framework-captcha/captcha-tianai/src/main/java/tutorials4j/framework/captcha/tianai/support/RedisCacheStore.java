package tutorials4j.framework.captcha.tianai.support;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.captcha.support.GraphicCaptchaCacheTemplate;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * 基于 Redis 的缓存存储实现，适配天意验证码的缓存接口。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class RedisCacheStore implements CacheStore {

  protected final GraphicCaptchaCacheTemplate captchaCacheTemplate;

  @Override
  public AnyMap getCache(String key) {
    String jsonData = captchaCacheTemplate.get(key);
    if (StringUtils.isEmpty(jsonData)) {
      return null;
    }
    return GsonUtils.toObject(jsonData, new TypeToken<AnyMap>() {}.getType());
  }

  @Override
  public AnyMap getAndRemoveCache(String key) {
    AnyMap data = getCache(key);
    if (data == null) {
      return null;
    }
    captchaCacheTemplate.delete(key);
    return data;
  }

  @Override
  public boolean setCache(String key, AnyMap data, Long expire, TimeUnit timeUnit) {
    String jsonData = GsonUtils.toJson(data);
    captchaCacheTemplate.put(key, jsonData);
    return true;
  }

  @Override
  public Long incr(String key, long delta, Long expire, TimeUnit timeUnit) {
    throw BaseErrorCode.METHOD_NOT_ALLOWED.throwed();
  }

  @Override
  public Long getLong(String key) {
    throw BaseErrorCode.METHOD_NOT_ALLOWED.throwed();
  }

  @Override
  public void close() throws Exception {}
}
