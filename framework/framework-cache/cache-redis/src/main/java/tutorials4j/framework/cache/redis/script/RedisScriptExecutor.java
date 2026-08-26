package tutorials4j.framework.cache.redis.script;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.script.RedisScript;
import tutorials4j.framework.cache.redis.RedisTemplateDecorator;

/**
 * Redis Lua 脚本执行器，封装常用的原子操作。
 *
 * <p>所有方法均基于 Lua 脚本实现，保证操作的原子性。内部缓存已加载的脚本实例，避免重复解析。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class RedisScriptExecutor {

  private final RedisTemplateDecorator redisTemplateDecorator;

  /** 缓存脚本类型与对应 RedisScript 实例的映射，提高执行效率。 */
  private final ConcurrentMap<ScriptType, RedisScript<Long>> scripts = new ConcurrentHashMap<>();

  /**
   * 原子性“检查并设置”（Compare-And-Set）。
   *
   * <p>仅当 {@code key} 的当前值等于 {@code expectedValue} 时，将其更新为 {@code newValue}。
   *
   * @param key Redis 键，不能为 null
   * @param expectedValue 期望的旧值，不能为 null
   * @param newValue 要设置的新值，不能为 null
   * @return true 表示更新成功（当前值匹配且已修改），false 表示失败（值不匹配或键不存在）
   * @throws IllegalArgumentException 如果任一参数为 null
   */
  public boolean checkAndSet(String key, String expectedValue, String newValue) {
    assertNotNull(key, expectedValue, newValue);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.CHECK_AND_SET, ScriptType::getScript);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(key), expectedValue, newValue);
    return result != null && result >= 1;
  }

  /**
   * 原子性“检查并删除”。
   *
   * <p>仅当 {@code key} 的当前值等于 {@code value} 时，删除该键。
   *
   * @param key Redis 键，不能为 null
   * @param value 期望的值，不能为 null
   * @return true 表示删除成功（键存在且值匹配），false 表示失败（键不存在或值不匹配）
   * @throws IllegalArgumentException 如果任一参数为 null
   */
  public boolean deleteIfSame(String key, String value) {
    assertNotNull(key, value);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.DELETE_IF_SAME, ScriptType::getScript);

    Long result =
        redisTemplateDecorator.getStringRedisTemplate().execute(script, List.of(key), value);
    return result != null && result >= 1;
  }

  /**
   * 原子性“仅当键不存在时设置”（带过期时间）。
   *
   * <p>若 {@code key} 不存在，则设置值为 {@code value}，并指定过期时间（毫秒）。
   *
   * @param key Redis 键，不能为 null
   * @param value 要设置的值，不能为 null
   * @param expireMills 过期时间（毫秒），必须大于 0
   * @return true 表示设置成功（键之前不存在），false 表示键已存在，未做任何操作
   * @throws IllegalArgumentException 如果 key 或 value 为 null
   */
  public boolean setIfAbsent(String key, String value, long expireMills) {
    assertNotNull(key, value);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.SET_IF_ABSENT, ScriptType::getScript);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(key), value, String.valueOf(expireMills));
    return result != null && result >= 1;
  }

  /**
   * 原子性“检查并重置过期时间”（锁续期）。
   *
   * <p>仅当 {@code key} 的当前值等于 {@code value} 时，将该键的过期时间重置为 {@code expireMills} 毫秒。
   *
   * @param key Redis 键，不能为 null
   * @param value 期望的值，不能为 null
   * @param expireMills 新的过期时间（毫秒），必须大于 0
   * @return true 表示续期成功（键存在且值匹配，过期时间已更新），false 表示失败（键不存在、值不匹配或 PEXPIRE 返回 0）
   * @throws IllegalArgumentException 如果 key 或 value 为 null
   */
  public boolean checkAndResetExpire(String key, String value, long expireMills) {
    assertNotNull(key, value);
    RedisScript<Long> script =
        scripts.computeIfAbsent(ScriptType.CHECK_AND_RESET_EXPIRE, ScriptType::getScript);

    Long result =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(script, List.of(key), value, String.valueOf(expireMills));
    return result != null && result >= 1;
  }

  /**
   * 断言传入的参数均不为 null，否则抛出 IllegalArgumentException。
   *
   * @param args 待检查的参数列表
   * @throws IllegalArgumentException 如果任一参数为 null
   */
  private void assertNotNull(Object... args) {
    if (ObjectUtils.anyNull(args)) {
      throw new IllegalArgumentException("Arguments must not be null");
    }
  }
}
