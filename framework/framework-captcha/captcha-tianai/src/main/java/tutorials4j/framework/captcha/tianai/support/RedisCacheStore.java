package tutorials4j.framework.captcha.tianai.support;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.captcha.support.GraphicCaptchaCacheTemplate;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * 基于 Redis 的缓存存储实现，适配天意验证码的缓存接口。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class RedisCacheStore implements CacheStore {

  /** 图形验证码缓存操作模板 */
  protected final GraphicCaptchaCacheTemplate captchaCacheTemplate;

  /** 根据 key 获取缓存数据，不存在时返回 null。 */
  @Override
  public AnyMap getCache(String key) {
    String jsonData = captchaCacheTemplate.get(key);
    if (StringUtils.isEmpty(jsonData)) {
      return null;
    }
    return GsonUtils.toObject(jsonData, new TypeToken<AnyMap>() {}.getType());
  }

  /** 根据 key 获取并删除缓存数据，不存在时返回 null。 */
  @Override
  public AnyMap getAndRemoveCache(String key) {
    AnyMap data = getCache(key);
    if (data == null) {
      return null;
    }
    captchaCacheTemplate.delete(key);
    return data;
  }

  /** 将数据写入缓存并返回是否成功。 */
  @Override
  public boolean setCache(String key, AnyMap data, Long expire, TimeUnit timeUnit) {
    String jsonData = GsonUtils.toJson(data);
    captchaCacheTemplate.put(key, jsonData);
    return true;
  }

  /** 不支持的自增操作，抛出 {@link UnsupportedOperationException}。 */
  @Override
  public Long incr(String key, long delta, Long expire, TimeUnit timeUnit) {
    throw new UnsupportedOperationException("incr");
  }

  /** 不支持的数值获取操作，抛出 {@link UnsupportedOperationException}。 */
  @Override
  public Long getLong(String key) {
    throw new UnsupportedOperationException("getLong");
  }

  /** 关闭缓存存储，当前实现为空操作。 */
  @Override
  public void close() throws Exception {}
}
