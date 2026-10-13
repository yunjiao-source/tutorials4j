package tutorials4j.toolkit.cache.redis.component;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ToolkitKeySerializer} 单元测试：统一 key 前缀。
 *
 * @author YunJiao Yang
 */
class ToolkitKeySerializerTest {
  private final ToolkitKeySerializer serializer = new ToolkitKeySerializer("template:");

  @Test
  @DisplayName("序列化时拼接前缀")
  void shouldPrefixKeyOnSerialize() {
    assertThat(new String(serializer.serialize("user:1"), UTF_8)).isEqualTo("template:user:1");
    assertThat(new String(serializer.serialize(""), UTF_8)).isEqualTo("template:");
  }

  @Test
  @DisplayName("按 UTF-8 编码中文 key")
  void shouldEncodeWithUtf8() {
    String key = "用户:1";

    assertThat(serializer.serialize(key)).isEqualTo(("template:" + key).getBytes(UTF_8));
    assertThat(serializer.deserialize(serializer.serialize(key))).isEqualTo("template:" + key);
  }

  @Test
  @DisplayName("前缀为空字符串时等同于原 key")
  void shouldKeepKeyWhenPrefixIsEmpty() {
    ToolkitKeySerializer noPrefix = new ToolkitKeySerializer("");

    assertThat(new String(noPrefix.serialize("user:1"), UTF_8)).isEqualTo("user:1");
  }

  @Test
  @DisplayName("现状记录：反序列化不会去掉前缀（继承父类实现）")
  void shouldNotStripPrefixOnDeserialize() {
    byte[] bytes = serializer.serialize("user:1");

    assertThat(serializer.deserialize(bytes)).isEqualTo("template:user:1");
  }

  @Test
  @DisplayName("现状记录：serialize(null) 由父类的 null 变为前缀 + \"null\" 文本")
  void shouldSerializeNullAsPrefixedLiteral() {
    // 父类 StringRedisSerializer 对 null 返回 null，此处因字符串拼接会得到 "template:null"
    assertThat(serializer.serialize(null)).isEqualTo("template:null".getBytes(UTF_8));
  }
}
