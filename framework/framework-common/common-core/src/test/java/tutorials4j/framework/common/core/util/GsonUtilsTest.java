package tutorials4j.framework.common.core.util;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** GsonUtils 单元测试 */
class GsonUtilsTest {

  // 测试用数据类
  static class Person {
    private String name;
    private int age;
    private Date birth; // 用于测试日期格式
    private String address; // 用于测试 null 序列化

    public Person() {}

    public Person(String name, int age, Date birth, String address) {
      this.name = name;
      this.age = age;
      this.birth = birth;
      this.address = address;
    }

    // getter/setter 省略（Gson 通过反射访问字段，不需要显式提供）
  }

  // ---------------------- getInstance() ----------------------
  @Test
  void testGetInstance_ShouldReturnSameInstance() {
    Gson instance1 = GsonUtils.getInstance();
    Gson instance2 = GsonUtils.getInstance();
    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }

  // ---------------------- toJsonElement() ----------------------
  @Test
  void testToJsonElement_ValidJson_ShouldReturnJsonElement() {
    String json = "{\"key\":\"value\"}";
    JsonElement element = GsonUtils.toJsonElement(json);
    assertTrue(element.isJsonObject());
    assertEquals("value", element.getAsJsonObject().get("key").getAsString());
  }

  @Test
  void testToJsonElement_InvalidJson_ShouldThrowException() {
    String invalidJson = "{invalid}";
    assertThrows(JsonSyntaxException.class, () -> GsonUtils.toJsonElement(invalidJson));
  }

  // ---------------------- toJsonArray() ----------------------
  @Test
  void testToJsonArray_ValidJsonArray_ShouldReturnJsonArray() {
    String jsonArray = "[1,2,3]";
    JsonArray array = GsonUtils.toJsonArray(jsonArray);
    assertEquals(3, array.size());
    assertEquals(1, array.get(0).getAsInt());
  }

  @Test
  void testToJsonArray_InvalidJsonArray_ShouldThrowException() {
    String notArray = "{\"key\":\"value\"}";
    assertThrows(IllegalStateException.class, () -> GsonUtils.toJsonArray(notArray));
  }

  // ---------------------- toJsonObject() ----------------------
  @Test
  void testToJsonObject_ValidJsonObject_ShouldReturnJsonObject() {
    String json = "{\"name\":\"John\"}";
    JsonObject object = GsonUtils.toJsonObject(json);
    assertEquals("John", object.get("name").getAsString());
  }

  @Test
  void testToJsonObject_InvalidJsonObject_ShouldThrowException() {
    String notObject = "[1,2,3]";
    assertThrows(IllegalStateException.class, () -> GsonUtils.toJsonObject(notObject));
  }

  // ---------------------- toJson() ----------------------
  @Test
  void testToJson_ObjectWithNull_ShouldSerializeNull() {
    Person person = new Person("Alice", 30, null, null);
    String json = GsonUtils.toJson(person);
    assertTrue(json.contains("\"address\":null"));
    assertTrue(json.contains("\"birth\":null"));
  }

  @Test
  void testToJson_ObjectWithDate_ShouldUseConfiguredDateFormat() {
    // 假设 DefaultConsts.DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    Date date = new Date(1672531200000L); // 2023-01-01 00:00:00 UTC
    Person person = new Person("Bob", 25, date, "NYC");
    String json = GsonUtils.toJson(person);
    // 简单验证日期部分包含格式化的字符串（具体值取决于时区，这里只验证非时间戳）
    assertTrue(json.contains("birth\":\"2023-01-01"));
  }

  @Test
  void testToJson_NullObject_ShouldReturnNullString() {
    assertEquals("null", GsonUtils.toJson(null));
  }

  // ---------------------- toObject(Class) ----------------------
  @Test
  void testToObject_ValidJson_ShouldDeserialize() {
    String json = "{\"name\":\"Tom\",\"age\":22,\"address\":\"LA\"}";
    Person person = GsonUtils.toObject(json, Person.class);
    assertEquals("Tom", person.name);
    assertEquals(22, person.age);
    assertEquals("LA", person.address);
  }

  @Test
  void testToObject_JsonWithDate_ShouldParseConfiguredFormat() {
    String json = "{\"birth\":\"2023-01-01 12:00:00\"}";
    Person person = GsonUtils.toObject(json, Person.class);
    assertNotNull(person.birth);
    // 简单验证年份（注意时区影响，这里只做非空检查）
  }

  @Test
  void testToObject_InvalidJson_ShouldThrowException() {
    String invalid = "not json";
    assertThrows(JsonSyntaxException.class, () -> GsonUtils.toObject(invalid, Person.class));
  }

  // ---------------------- toObject(Type) ----------------------
  @Test
  void testToObject_WithTypeToken_ShouldDeserializeGenericList() {
    String json = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
    Type listType = new com.google.gson.reflect.TypeToken<List<Person>>() {}.getType();
    List<Person> persons = GsonUtils.toObject(json, listType);
    assertEquals(2, persons.size());
    assertEquals("A", persons.get(0).name);
  }

  // ---------------------- toList() (存在设计缺陷，需强制转换) ----------------------
  @Test
  @SuppressWarnings("unchecked")
  void testToList_ShouldReturnListOfGivenType() {
    String json = "[\"hello\",\"world\"]";
    // 实际返回 List<String>，但方法签名返回 T，需要强制转换
    List<String> list = GsonUtils.toList(json, String.class);
    assertEquals(2, list.size());
    assertEquals("hello", list.get(0));
    assertEquals("world", list.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testToList_WithNumbers_ShouldDeserializeCorrectly() {
    String json = "[1,2,3]";
    List<Integer> list = GsonUtils.toList(json, Integer.class);
    assertEquals(3, list.size());
    assertEquals(1, list.get(0));
  }

  // ---------------------- toListMap() ----------------------
  @Test
  void testToListMap_ValidJson_ShouldReturnListOfMaps() {
    String json = "[{\"a\":1,\"b\":2},{\"c\":3}]";
    List<Map<String, String>> listMap = GsonUtils.toListMap(json);
    assertEquals(2, listMap.size());
    assertEquals("1", listMap.get(0).get("a"));
    assertEquals("2", listMap.get(0).get("b"));
    assertEquals("3", listMap.get(1).get("c"));
  }

  @Test
  void testToListMap_EmptyArray_ShouldReturnEmptyList() {
    String json = "[]";
    List<Map<String, String>> listMap = GsonUtils.toListMap(json);
    assertTrue(listMap.isEmpty());
  }

  // ---------------------- toMaps() ----------------------
  @Test
  void testToMaps_WithIntegerValues_ShouldReturnMap() {
    String json = "{\"one\":1,\"two\":2}";
    // 需要显式指定 T 为 Integer，这里通过赋值时指定泛型
    Map<String, Integer> map = GsonUtils.toMaps(json, Integer.class);
    assertEquals(2, map.size());
    assertEquals(1, map.get("one"));
    assertEquals(2, map.get("two"));
  }

  @Test
  void testToMaps_WithStringValues_ShouldReturnMap() {
    String json = "{\"x\":\"foo\",\"y\":\"bar\"}";
    Map<String, String> map = GsonUtils.toMaps(json, String.class);
    assertEquals("foo", map.get("x"));
    assertEquals("bar", map.get("y"));
  }

  @Test
  void testToMaps_InvalidJson_ShouldThrowException() {
    String invalid = "not a map";
    assertThrows(JsonSyntaxException.class, () -> GsonUtils.toMaps(invalid, String.class));
  }
}
