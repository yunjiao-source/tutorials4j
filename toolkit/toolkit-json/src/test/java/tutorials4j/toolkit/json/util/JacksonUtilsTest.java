package tutorials4j.toolkit.json.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tutorials4j.toolkit.json.component.ToolkitSimpleModule;

/**
 * {@link JacksonUtils} 单元测试：静态单例上的读写、类型转换与异常行为。
 *
 * @author YunJiao Yang
 */
class JacksonUtilsTest {
  private final JacksonUtils jacksonUtils = JacksonUtils.instance;

  @BeforeEach
  void setUp() {
    jacksonUtils.setObjectMapper(new ObjectMapper());
  }

  static class User {
    private String name;
    private Integer age;

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
  }

  private static ObjectMapper mapperWithToolkitModule() {
    return JsonMapper.builder().addModule(new ToolkitSimpleModule()).build();
  }

  @Test
  @DisplayName("对象与 JSON 字符串互转")
  void shouldRoundTripObject() {
    User user = new User();
    user.setName("alice");
    user.setAge(20);

    String json = jacksonUtils.toJson(user);
    User parsed = jacksonUtils.toObject(json, User.class);

    assertThat(json).contains("\"name\":\"alice\"").contains("\"age\":20");
    assertThat(parsed.getName()).isEqualTo("alice");
    assertThat(parsed.getAge()).isEqualTo(20);
  }

  @Test
  @DisplayName("Map 可直接转换为对象")
  void shouldConvertMapToObject() {
    Map<String, Object> map = Map.of("name", "alice", "age", 20);

    User user = jacksonUtils.toObject(map, User.class);

    assertThat(user.getName()).isEqualTo("alice");
    assertThat(user.getAge()).isEqualTo(20);
  }

  @Test
  @DisplayName("convertValue 支持任意对象到目标类型")
  void shouldConvertValue() {
    User user = jacksonUtils.convertValue(Map.of("name", "bob", "age", 30), User.class);

    assertThat(user.getName()).isEqualTo("bob");
    assertThat(jacksonUtils.convertValue(user, Map.class)).containsEntry("name", "bob");
  }

  @Test
  @DisplayName("对象与 JSON 字符串均可解析为 JsonNode")
  void shouldConvertToNode() {
    User user = new User();
    user.setName("alice");

    JsonNode fromDomain = jacksonUtils.toNode(user);
    JsonNode fromJson = jacksonUtils.toNode("{\"name\":\"alice\",\"age\":20}");

    assertThat(fromDomain.get("name").asString()).isEqualTo("alice");
    assertThat(fromJson.get("age").asInt()).isEqualTo(20);
  }

  @Test
  @DisplayName("JsonParser 可解析为 JsonNode 并交给 treeToValue")
  void shouldReadFromParserAndConvertTree() {
    try (JsonParser parser = jacksonUtils.createParser("{\"name\":\"carol\"}")) {
      JsonNode node = jacksonUtils.toNode(parser);

      assertThat(node.get("name").asString()).isEqualTo("carol");
      assertThat(jacksonUtils.treeToValue(node, User.class).getName()).isEqualTo("carol");
    }
  }

  @Test
  @DisplayName("按 TypeReference 反序列化")
  void shouldDeserializeWithTypeReference() {
    List<User> users =
        jacksonUtils.toObject("[{\"name\":\"a\"}]", new TypeReference<List<User>>() {});

    assertThat(users).hasSize(1);
    assertThat(users.get(0).getName()).isEqualTo("a");
  }

  @Test
  @DisplayName("按 JavaType 反序列化，getTypeFactory 可构造参数化类型")
  void shouldDeserializeWithJavaType() {
    JavaType javaType =
        jacksonUtils.getTypeFactory().constructParametricType(List.class, User.class);

    List<User> users = jacksonUtils.toObject("[{\"name\":\"a\"},{\"name\":\"b\"}]", javaType);

    assertThat(users).hasSize(2);
    assertThat(jacksonUtils.getTypeFactory().constructType(User.class).getRawClass())
        .isEqualTo(User.class);
  }

  @Test
  @DisplayName("toList 按元素类型反序列化集合")
  void shouldDeserializeList() {
    List<User> users = jacksonUtils.toList("[{\"name\":\"a\"},{\"name\":\"b\"}]", User.class);

    assertThat(users).extracting(User::getName).containsExactly("a", "b");
  }

  @Test
  @DisplayName("toSet 按元素类型反序列化集合并去重")
  void shouldDeserializeSet() {
    Set<User> users =
        jacksonUtils.toSet("[{\"name\":\"a\",\"age\":1},{\"name\":\"a\",\"age\":1}]", User.class);

    assertThat(users).hasSize(2);
    assertThat(users).extracting(User::getName).containsOnly("a");
  }

  @Test
  @DisplayName("toMap 支持无类型参数与指定键值类型")
  void shouldDeserializeMap() {
    Map<String, Object> raw = jacksonUtils.toMap("{\"name\":\"alice\",\"age\":20}");
    Map<String, User> typed =
        jacksonUtils.toMap("{\"u\":{\"name\":\"alice\"}}", String.class, User.class);

    assertThat(raw).containsEntry("name", "alice").containsEntry("age", 20);
    assertThat(typed.get("u").getName()).isEqualTo("alice");
  }

  @Test
  @DisplayName("toArray 按元素类型反序列化数组")
  void shouldDeserializeArray() {
    User[] users = jacksonUtils.toArray("[{\"name\":\"a\"},{\"name\":\"b\"}]", User.class);

    assertThat(users).hasSize(2);
    assertThat(users[0].getName()).isEqualTo("a");
  }

  @Test
  @DisplayName("现状记录：toArray(String) 无法还原元素类型，返回 Object[] 且元素为 Map")
  void shouldNotResolveElementTypeInGenericToArray() {
    Object array = jacksonUtils.toArray("[{\"name\":\"a\"}]");

    assertThat(array).isInstanceOf(Object[].class);
    assertThat(((Object[]) array)[0]).isInstanceOf(Map.class);
  }

  @Test
  @DisplayName("非法 JSON 抛出 JacksonException")
  void shouldThrowOnInvalidJson() {
    assertThatThrownBy(() -> jacksonUtils.toObject("{invalid", User.class))
        .isInstanceOf(JacksonException.class);
    assertThatThrownBy(() -> jacksonUtils.toNode("{invalid")).isInstanceOf(JacksonException.class);
  }

  @Test
  @DisplayName("setObjectMapper 会替换底层 ObjectMapper（静态单例可被自动装配改写）")
  void shouldUseReplacedObjectMapper() {
    assertThat(jacksonUtils.toJson(1L)).isEqualTo("1");

    jacksonUtils.setObjectMapper(mapperWithToolkitModule());

    assertThat(jacksonUtils.toJson(1L)).isEqualTo("\"1\"");
  }
}
