package tutorials4j.framework.captcha.tianai;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.captcha.GraphicCaptchaCacheTemplate;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * TODO
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
    throw new FrameworkRuntimeException("方法不支持");
  }

  @Override
  public Long getLong(String key) {
    throw new FrameworkRuntimeException("方法不支持");
  }

  @Override
  public void close() throws Exception {}
}
