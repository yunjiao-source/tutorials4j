package tutorials4j.framework.auth.core.apikey;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Triple;
import tutorials4j.framework.auth.core.autoconfigure.ApiKeyRateLimiterOptions;
import tutorials4j.framework.auth.core.autoconfigure.ApiKeyRateLimiterOptions.ApiKeyLimiterRequestOptions;
import tutorials4j.framework.cache.redis.script.RedisScriptExecutor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class ApiKeyRateLimiterService {
  private final RedisScriptExecutor redisScriptExecutor;
  private final ApiKeyRateLimiterOptions apiKeyRateLimiterOptions;

  public Triple<Long, Long, Duration> getRateLimiter(String limiterName, String apiKeyValue) {
    ApiKeyLimiterRequestOptions options = getRateLimiterRequestOptions(limiterName);
    String key = generateKey(limiterName, apiKeyValue);

    Duration timeWindow = options.getTimeWindow();
    long count = redisScriptExecutor.incrAndExpire(key, timeWindow.toMillis());
    return Triple.of(count, options.getMaxRequestCount(), timeWindow);
  }

  private String generateKey(String limiterName, String apiKeyValue) {
    String keyPrefix = apiKeyRateLimiterOptions.getKeyPrefix();
    return keyPrefix + limiterName + ":" + apiKeyValue;
  }

  private ApiKeyLimiterRequestOptions getRateLimiterRequestOptions(String limiterName) {
    ApiKeyLimiterRequestOptions options =
        apiKeyRateLimiterOptions.getNamedLimiter().get(limiterName);
    return options == null ? apiKeyRateLimiterOptions.getDefaultLimiter() : options;
  }
}
