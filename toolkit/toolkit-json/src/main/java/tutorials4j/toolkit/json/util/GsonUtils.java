package tutorials4j.toolkit.json.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class GsonUtils {
  private final Gson gson;

  private GsonUtils(Gson gson) {
    this.gson = gson;
  }

  public static GsonUtils create(Supplier<GsonBuilder> gsonBuilderSupplier) {
    GsonBuilder gsonBuilder = gsonBuilderSupplier.get();
    return new GsonUtils(gsonBuilder.create());
  }

  // 静态内部类持有者：延迟加载默认单例
  private static class Holder {
    private static final GsonUtils INSTANCE =
        create(
            () -> {
              return new GsonBuilder()
                  .enableComplexMapKeySerialization()
                  .serializeNulls()
                  .disableHtmlEscaping();
            });
  }

  // 获取默认单例
  public static GsonUtils getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * 将 JSON 字符串解析为 JsonElement。
   *
   * @param content JSON 字符串
   * @return 解析后的 JsonElement
   */
  public JsonElement toJsonElement(String content) {
    return JsonParser.parseString(content);
  }

  /**
   * 将 JSON 字符串解析为 JsonArray。
   *
   * @param content JSON 数组字符串
   * @return 解析后的 JsonArray
   */
  public JsonArray toJsonArray(String content) {
    return toJsonElement(content).getAsJsonArray();
  }

  /**
   * 将 JSON 字符串解析为 JsonObject。
   *
   * @param content JSON 对象字符串
   * @return 解析后的 JsonObject
   */
  public JsonObject toJsonObject(String content) {
    return toJsonElement(content).getAsJsonObject();
  }

  /**
   * 将对象序列化为 JSON 字符串。
   *
   * @param domain 待序列化对象
   * @return JSON 字符串
   */
  public <T> String toJson(T domain) {
    return gson.toJson(domain);
  }

  /**
   * 将 JSON 字符串反序列化为指定 Class 类型的对象。
   *
   * @param content JSON 字符串
   * @param valueType 目标类型
   * @return 反序列化后的对象
   */
  public <T> T toObject(String content, Class<T> valueType) {
    return gson.fromJson(content, valueType);
  }

  /**
   * 将 JSON 字符串反序列化为指定 Type 类型的对象。
   *
   * @param content JSON 字符串
   * @param typeOfT 目标泛型类型
   * @return 反序列化后的对象
   */
  public <T> T toObject(String content, Type typeOfT) {
    return gson.fromJson(content, typeOfT);
  }

  /**
   * 将 JSON 数组字符串反序列化为指定元素类型的 List。
   *
   * @param content JSON 数组字符串
   * @param valueType 列表元素类型
   * @return 反序列化后的 List
   */
  public <T> List<T> toList(String content, Class<T> valueType) {
    Type type = TypeToken.getParameterized(List.class, valueType).getType();
    return gson.fromJson(content, type);
  }

  /**
   * 将 JSON 数组字符串反序列化为 List&lt;Map&lt;String, String&gt;&gt;。
   *
   * @param content JSON 数组字符串
   * @return 反序列化后的 List
   */
  public <T> List<Map<String, T>> toListMap(String content) {
    return gson.fromJson(content, new TypeToken<List<Map<String, String>>>() {}.getType());
  }

  /**
   * 将 JSON 对象字符串反序列化为指定值类型的 Map&lt;String, T&gt;。
   *
   * @param content JSON 对象字符串
   * @param valueType Map 值类型
   * @return 反序列化后的 Map
   */
  public <T> Map<String, T> toMaps(String content, Class<T> valueType) {
    Type type = TypeToken.getParameterized(Map.class, String.class, valueType).getType();
    return gson.fromJson(content, type);
  }
}
