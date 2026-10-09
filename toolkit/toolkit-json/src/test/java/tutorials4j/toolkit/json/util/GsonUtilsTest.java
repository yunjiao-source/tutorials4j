package tutorials4j.toolkit.json.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link GsonUtils} 单元测试：单例配置、序列化/反序列化与异常行为。
 *
 * @author Yun Jiao
 */
class GsonUtilsTest {
  private static final GsonUtils GSON = GsonUtils.getInstance();

  static class User {
    private String name;
    private Integer age;
    private String remark;

    User() {}

    User(String name, Integer age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Integer getAge() {
      return age;
    }

    public void setAge(Integer age) {
      this.age = age;
    }

    public String getRemark() {
      return remark;
    }

    public void setRemark(String remark) {
      this.remark = remark;
    }
  }

  @Test
  @DisplayName("getInstance 返回同一个单例")
  void shouldReturnSameSingletonInstance() {
    assertThat(GsonUtils.getInstance()).isSameAs(GSON);
    assertThat(GsonUtils.getInstance()).isSameAs(GsonUtils.getInstance());
  }

  @Test
  @DisplayName("默认单例序列化 null 字段")
  void shouldSerializeNullFieldsByDefault() {
    User user = new User("alice", 20);

    String json = GSON.toJson(user);

    assertThat(json).contains("\"name\":\"alice\"");
    assertThat(json).contains("\"age\":20");
    assertThat(json).contains("\"remark\":null");
  }

  @Test
  @DisplayName("默认单例不转义 HTML 字符")
  void shouldNotEscapeHtmlByDefault() {
    User user = new User("<a href=\"/x\">&</a>", 1);

    String json = GSON.toJson(user);

    assertThat(json).contains("<a href=").contains("&").doesNotContain("\\u003c");
  }

  @Test
  @DisplayName("默认单例启用复杂 Map 键序列化")
  void shouldSerializeComplexMapKeys() {
    Map<User, String> map = new LinkedHashMap<>();
    map.put(new User("alice", 20), "value");

    String json = GSON.toJson(map);

    assertThat(json).contains("name").contains("alice").contains("value");
  }

  @Test
  @DisplayName("create 使用传入的 GsonBuilder 配置且只调用一次 supplier")
  void shouldUseProvidedGsonBuilder() {
    AtomicInteger calls = new AtomicInteger();

    GsonUtils custom =
        GsonUtils.create(
            () -> {
              calls.incrementAndGet();
              return new GsonBuilder();
            });

    assertThat(calls).hasValue(1);
    // 未开启 serializeNulls，null 字段被忽略
    assertThat(custom.toJson(new User("alice", 20))).doesNotContain("remark");
    assertThat(custom).isNotSameAs(GSON);
  }

  @Test
  @DisplayName("对象与 JSON 字符串互转")
  void shouldRoundTripObject() {
    User user = new User("alice", 20);

    String json = GSON.toJson(user);
    User parsed = GSON.toObject(json, User.class);

    assertThat(parsed.getName()).isEqualTo("alice");
    assertThat(parsed.getAge()).isEqualTo(20);
  }

  @Test
  @DisplayName("按 Type 反序列化")
  void shouldDeserializeByType() {
    Map<String, Integer> map =
        GSON.toObject("{\"a\":1,\"b\":2}", new TypeToken<Map<String, Integer>>() {}.getType());

    assertThat(map).containsEntry("a", 1).containsEntry("b", 2);
  }

  @Test
  @DisplayName("反序列化为 List")
  void shouldDeserializeList() {
    List<User> users = GSON.toList("[{\"name\":\"a\",\"age\":1},{\"name\":\"b\"}]", User.class);

    assertThat(users).hasSize(2);
    assertThat(users.get(0).getName()).isEqualTo("a");
    assertThat(users.get(0).getAge()).isEqualTo(1);
    assertThat(users.get(1).getAge()).isNull();
  }

  @Test
  @DisplayName("反序列化为 List<Map>")
  void shouldDeserializeListMap() {
    List<Map<String, String>> maps = GSON.toListMap("[{\"k\":\"v\"},{\"k2\":\"v2\"}]");

    assertThat(maps).hasSize(2);
    assertThat(maps.get(0)).containsEntry("k", "v");
    assertThat(maps.get(1)).containsEntry("k2", "v2");
  }

  @Test
  @DisplayName("现状记录：toListMap 固定按 Map<String,String> 解析，泛型 T 不参与")
  void shouldAlwaysParseListMapValuesAsString() {
    Object value = GSON.<Integer>toListMap("[{\"k\":\"1\"}]").get(0).get("k");

    assertThat(value).isInstanceOf(String.class).isEqualTo("1");
  }

  @Test
  @DisplayName("反序列化为 Map 并指定值类型")
  void shouldDeserializeMaps() {
    Map<String, User> users = GSON.toMaps("{\"first\":{\"name\":\"a\",\"age\":1}}", User.class);

    assertThat(users).containsOnlyKeys("first");
    assertThat(users.get("first").getName()).isEqualTo("a");
  }

  @Test
  @DisplayName("解析为 JsonElement / JsonArray / JsonObject")
  void shouldParseJsonElements() {
    assertThat(GSON.toJsonElement("{\"a\":1}").getAsJsonObject().get("a").getAsInt()).isEqualTo(1);

    JsonArray array = GSON.toJsonArray("[1,2,3]");
    assertThat(array).hasSize(3);
    assertThat(array.get(2).getAsInt()).isEqualTo(3);

    JsonObject object = GSON.toJsonObject("{\"a\":{\"b\":2}}");
    assertThat(object.getAsJsonObject("a").get("b").getAsInt()).isEqualTo(2);
  }

  @Test
  @DisplayName("非法 JSON 抛出 JsonSyntaxException")
  void shouldThrowOnInvalidJson() {
    assertThatThrownBy(() -> GSON.toObject("{invalid", User.class))
        .isInstanceOf(JsonSyntaxException.class);
    assertThatThrownBy(() -> GSON.toJsonElement("{invalid"))
        .isInstanceOf(JsonSyntaxException.class);
  }

  @Test
  @DisplayName("类型不匹配时抛出 IllegalStateException")
  void shouldThrowWhenElementTypeMismatch() {
    assertThatThrownBy(() -> GSON.toJsonArray("{\"a\":1}"))
        .isInstanceOf(IllegalStateException.class);
    assertThatThrownBy(() -> GSON.toJsonObject("[1,2]")).isInstanceOf(IllegalStateException.class);
  }
}
