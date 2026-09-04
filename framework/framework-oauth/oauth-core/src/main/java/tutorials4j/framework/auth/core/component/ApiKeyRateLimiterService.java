package tutorials4j.framework.auth.core.component;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.auth.core.autoconfigure.ApiKeyOptions;
import tutorials4j.framework.auth.core.autoconfigure.ApiKeyOptions.ApiKeyLimiterOptions;
import tutorials4j.framework.cache.redis.script.RedisScriptExecutor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class ApiKeyRateLimiterService {
  private static final String MINUTE = "minute:";
  private static final String DAILY = "daily:";
  private final RedisScriptExecutor redisScriptExecutor;
  private final ApiKeyOptions apiKeyOptions;

  public boolean allowMinuteRequest(String limiterName, String apiKeyValue) {
    ApiKeyLimiterOptions options = getLimiterOptions(limiterName);
    String key = generateKey(MINUTE, apiKeyValue);

    long count = redisScriptExecutor.incrAndExpire(key, Duration.ofMinutes(1).toMillis());
    return count <= options.getMaxRequestsPerMinute();
  }

  public boolean allowDailyRequest(String limiterName, String apiKeyValue) {
    ApiKeyLimiterOptions options = getLimiterOptions(limiterName);
    String key = generateKey(DAILY, apiKeyValue);

    long count = redisScriptExecutor.incrAndExpire(key, Duration.ofDays(1).toMillis());
    return count <= options.getMaxRequestsPerDay();
  }

  private String generateKey(String type, String apiKeyValue) {
    String keyPrefix = apiKeyOptions.getKeyPrefix();
    return keyPrefix + type + apiKeyValue;
  }

  private ApiKeyLimiterOptions getLimiterOptions(String limiterName) {
    ApiKeyLimiterOptions options = apiKeyOptions.getNamedLimiter().get(limiterName);
    return options == null ? apiKeyOptions.getDefaultLimiter() : options;
  }
}
