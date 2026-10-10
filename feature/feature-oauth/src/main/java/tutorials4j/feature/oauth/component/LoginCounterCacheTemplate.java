package tutorials4j.feature.oauth.component;

import static tutorials4j.feature.oauth.OAuthFeatureConsts.CACHE_NAME_LOGIN_FAIL_COUNTER;

import org.springframework.stereotype.Component;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.template.AbstractCounterCacheTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
public class LoginCounterCacheTemplate extends AbstractCounterCacheTemplate<String> {

  public LoginCounterCacheTemplate() {
    super(CACHE_NAME_LOGIN_FAIL_COUNTER, CacheType.redis);
  }

  public boolean check(String key) {
    long maxTimes = 3L;

    return super.check(key, maxTimes);
  }
}
