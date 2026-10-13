package tutorials4j.toolkit.cache.redis.script;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import tutorials4j.toolkit.cache.redis.component.RedisTemplateDecorator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class RedisScriptExecutor {
  private final RedisTemplateDecorator redisTemplateDecorator;

  /** 缓存脚本类型与对应 RedisScript 实例的映射，提高执行效率。 */
  private final ConcurrentMap<ScriptType, RedisScript<Long>> scripts = new ConcurrentHashMap<>();

  public boolean checkAndSet(String key, String expectedValue, String newValue) {
    String newKey = wrapKey(key);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.CHECK_AND_SET, ScriptType::getScriptWithLong);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(newKey), expectedValue, newValue);
    return result != null && result >= 1;
  }

  public boolean deleteIfSame(String key, String value) {
    String newKey = wrapKey(key);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.DELETE_IF_SAME, ScriptType::getScriptWithLong);

    Long result =
        redisTemplateDecorator.getStringRedisTemplate().execute(script, List.of(newKey), value);
    return result != null && result >= 1;
  }

  public boolean setIfAbsent(String key, String value, long expireMills) {
    String newKey = wrapKey(key);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.SET_IF_ABSENT, ScriptType::getScriptWithLong);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(newKey), value, String.valueOf(expireMills));
    return result != null && result >= 1;
  }

  public boolean checkAndResetExpire(String key, String value, long expireMills) {
    String newKey = wrapKey(key);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.CHECK_AND_RESET_EXPIRE, ScriptType::getScriptWithLong);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(newKey), value, String.valueOf(expireMills));
    return result != null && result >= 1;
  }

  public long incrAndExpire(String key, long expireMills) {
    String newKey = wrapKey(key);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.INCR_AND_EXPIRE, ScriptType::getScriptWithLong);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(newKey), String.valueOf(expireMills));
    return result == null ? 0 : result;
  }

  private String wrapKey(String key) {
    Assert.notNull(key, "key must not be null");
    return key;
  }
}
