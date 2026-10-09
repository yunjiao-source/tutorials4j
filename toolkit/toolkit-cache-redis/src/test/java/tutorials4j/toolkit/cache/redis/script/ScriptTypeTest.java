package tutorials4j.toolkit.cache.redis.script;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * {@link ScriptType} 单元测试：Lua 脚本资源加载与类型声明。
 *
 * @author Yun Jiao
 */
class ScriptTypeTest {

  @SuppressWarnings("unchecked")
  private static DefaultRedisScript<Long> loadScript(ScriptType scriptType) {
    RedisScript<Long> script = scriptType.getScriptWithLong();
    assertThat(script).isInstanceOf(DefaultRedisScript.class);

    DefaultRedisScript<Long> defaultRedisScript = (DefaultRedisScript<Long>) script;
    defaultRedisScript.afterPropertiesSet();
    return defaultRedisScript;
  }

  private static String scriptPath(ScriptType scriptType) {
    try {
      Field field = ScriptType.class.getDeclaredField("path");
      field.setAccessible(true);
      return (String) field.get(scriptType);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("读取 ScriptType.path 失败", e);
    }
  }

  @Test
  @DisplayName("包含全部脚本类型")
  void shouldExposeAllScriptTypes() {
    assertThat(ScriptType.values())
        .extracting(Enum::name)
        .containsExactly(
            "SET_IF_ABSENT",
            "CHECK_AND_SET",
            "DELETE_IF_SAME",
            "INCR_AND_EXPIRE",
            "CHECK_AND_RESET_EXPIRE");
  }

  @Test
  @DisplayName("脚本路径统一为 lua/*.lua")
  void shouldUseLuaResourcePath() {
    for (ScriptType scriptType : ScriptType.values()) {
      assertThat(scriptPath(scriptType)).startsWith("lua/").endsWith(".lua");
    }
  }

  @Test
  @DisplayName("每种脚本都能从 classpath 加载且结果类型为 Long")
  void shouldLoadScriptResource() {
    for (ScriptType scriptType : ScriptType.values()) {
      DefaultRedisScript<Long> script = loadScript(scriptType);

      assertThat(script.getResultType()).isEqualTo(Long.class);
      assertThat(script.getScriptAsString()).isNotBlank();
    }
  }

  @Test
  @DisplayName("各脚本内容互不相同（避免误指向同一文件）")
  void shouldLoadDistinctScriptContent() {
    Set<String> contents = new HashSet<>();
    for (ScriptType scriptType : ScriptType.values()) {
      contents.add(loadScript(scriptType).getScriptAsString());
    }

    assertThat(contents).hasSize(ScriptType.values().length);
  }

  @Test
  @DisplayName("每次调用返回新的脚本实例")
  void shouldCreateNewScriptInstanceEachTime() {
    assertThat(ScriptType.SET_IF_ABSENT.getScriptWithLong())
        .isNotSameAs(ScriptType.SET_IF_ABSENT.getScriptWithLong());
  }
}
