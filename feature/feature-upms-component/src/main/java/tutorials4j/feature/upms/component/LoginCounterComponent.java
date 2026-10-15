package tutorials4j.feature.upms.component;

import static tutorials4j.feature.upms.UpmsFeatureConsts.CACHE_NAME_LOGIN_FAIL_COUNTER;

import org.springframework.stereotype.Component;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.template.AbstractCounterCacheTemplate;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Component
public class LoginCounterComponent extends AbstractCounterCacheTemplate<String> {
  public LoginCounterComponent() {
    super(CACHE_NAME_LOGIN_FAIL_COUNTER, CacheType.redis);
  }

  public boolean check(String key) {
    long maxTimes = 3L;

    return super.check(key, maxTimes);
  }
}
