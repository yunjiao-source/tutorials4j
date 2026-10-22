package tutorials4j.feature.upms.component;

import static tutorials4j.feature.upms.UpmsFeatureConsts.CACHE_LOGIN_FAIL_COUNTER;

import org.springframework.stereotype.Component;
import tutorials4j.feature.core.properties.UpmsFeatureProperties;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.template.AbstractCounterCacheTemplate;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
public class LoginCounterComponent extends AbstractCounterCacheTemplate<String> {
  public static final int DEFAULT_MAX_LOGIN_FAIL_COUNT = 5;
  private final UpmsFeatureProperties properties;

  public LoginCounterComponent(UpmsFeatureProperties properties) {
    super(CACHE_LOGIN_FAIL_COUNTER, CacheType.redis);
    this.properties = properties;
  }

  public boolean check(String key) {
    long maxTimes = DEFAULT_MAX_LOGIN_FAIL_COUNT;
    if (properties.getMaxLoginFailCount() != null && properties.getMaxLoginFailCount() > 0) {
      maxTimes = properties.getMaxLoginFailCount();
    }

    return super.check(key, maxTimes);
  }
}
