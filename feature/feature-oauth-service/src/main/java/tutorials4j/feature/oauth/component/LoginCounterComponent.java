package tutorials4j.feature.oauth.component;

import static tutorials4j.feature.oauth.OAuthFeatureConsts.CACHE_NAME_LOGIN_FAIL_COUNTER;

import org.springframework.stereotype.Component;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.template.AbstractCounterCacheTemplate;
import tutorials4j.toolkit.core.model.UserModel;

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

  public void fail(UserModel userModel) {}

  public void success(UserModel userModel) {}
}
