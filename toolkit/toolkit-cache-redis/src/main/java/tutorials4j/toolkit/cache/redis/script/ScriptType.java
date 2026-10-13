package tutorials4j.toolkit.cache.redis.script;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum ScriptType {
  SET_IF_ABSENT("lua/set_if_absent.lua"),

  CHECK_AND_SET("lua/check_and_set.lua"),

  DELETE_IF_SAME("lua/delete_if_same.lua"),

  INCR_AND_EXPIRE("lua/incr_and_expire.lua"),

  CHECK_AND_RESET_EXPIRE("lua/check_and_reset_expire.lua");

  private final String path;

  ScriptType(String path) {
    this.path = path;
  }

  public RedisScript<Long> getScriptWithLong() {
    return getScript(Long.class);
  }

  private <T> RedisScript<T> getScript(Class<T> clazz) {
    Resource resource = new ClassPathResource(path);
    DefaultRedisScript<T> script = new DefaultRedisScript<>();
    script.setLocation(resource);
    script.setResultType(clazz);
    return script;
  }
}
