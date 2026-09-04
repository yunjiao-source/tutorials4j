package tutorials4j.framework.cache.redis.script;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Redis Lua 脚本类型枚举。
 *
 * <p>每个枚举值对应一个 Lua 脚本文件（位于 classpath:scripts/ 下）， 并负责构建对应的 {@link RedisScript} 实例，供 {@link
 * RedisScriptExecutor} 使用。
 *
 * @author Yun Jiao
 */
public enum ScriptType {

  /** 仅在键不存在时设置值（带过期时间），用于实现分布式锁的加锁操作。 */
  SET_IF_ABSENT("scripts/set_if_absent.lua"),

  /** 原子性条件更新（Compare-And-Set），仅当键当前值等于期望旧值时，才更新为新值。 */
  CHECK_AND_SET("scripts/check_and_set.lua"),

  /** 仅当键当前值等于给定值时，删除该键，用于安全释放锁。 */
  DELETE_IF_SAME("scripts/delete_if_same.lua"),

  INCR_AND_EXPIRE("scripts/incr_and_expire.lua"),

  /** 仅当键当前值等于给定值时，重置该键的过期时间（续期），用于锁续命。 */
  CHECK_AND_RESET_EXPIRE("scripts/check_and_reset_expire.lua");

  private final String path; // Lua 脚本在 classpath 下的相对路径

  ScriptType(String path) {
    this.path = path;
  }

  /**
   * 获取当前脚本类型对应的 {@link RedisScript} 实例。
   *
   * <p>脚本执行结果统一返回 {@link Long} 类型，1 表示成功，0 表示失败。
   *
   * @return RedisScript 实例，已设置脚本位置和返回类型
   */
  public RedisScript<Long> getScript() {
    Resource resource = new ClassPathResource(path);
    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    script.setLocation(resource);
    script.setResultType(Long.class);
    return script;
  }
}
