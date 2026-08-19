package tutorials4j.framework.common.core.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * Gson 工具类，提供线程安全的 Gson 单例及 JSON 与对象互转的便捷方法。
 *
 * @author Yun Jiao
 */
public class GsonUtils {
  private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
  private static volatile Gson instance;

  static {
    GSON_BUILDER.enableComplexMapKeySerialization();
    GSON_BUILDER.serializeNulls();
    GSON_BUILDER.setDateFormat(DefaultConsts.DATE_TIME_FORMAT);
    GSON_BUILDER.disableHtmlEscaping();
  }

  private GsonUtils() {}

  /**
   * 获取 Gson 单例实例（双重检查锁，线程安全）。
   *
   * @return Gson 实例
   */
  public static Gson getInstance() {

    if (ObjectUtils.isEmpty(instance)) {
      synchronized (GSON_BUILDER) {
        if (ObjectUtils.isEmpty(instance)) {
          instance = GSON_BUILDER.create();
        }
      }
    }

    return instance;
  }

  /**
   * 将 JSON 字符串解析为 JsonElement。
   *
   * @param content JSON 字符串
   * @return 解析后的 JsonElement
   */
  public static JsonElement toJsonElement(String content) {
    return JsonParser.parseString(content);
  }

  /**
   * 将 JSON 字符串解析为 JsonArray。
   *
   * @param content JSON 数组字符串
   * @return 解析后的 JsonArray
   */
  public static JsonArray toJsonArray(String content) {
    return toJsonElement(content).getAsJsonArray();
  }

  /**
   * 将 JSON 字符串解析为 JsonObject。
   *
   * @param content JSON 对象字符串
   * @return 解析后的 JsonObject
   */
  public static JsonObject toJsonObject(String content) {
    return toJsonElement(content).getAsJsonObject();
  }

  /**
   * 将对象序列化为 JSON 字符串。
   *
   * @param domain 待序列化对象
   * @return JSON 字符串
   */
  public static <T> String toJson(T domain) {
    return getInstance().toJson(domain);
  }

  /**
   * 将 JSON 字符串反序列化为指定 Class 类型的对象。
   *
   * @param content JSON 字符串
   * @param valueType 目标类型
   * @return 反序列化后的对象
   */
  public static <T> T toObject(String content, Class<T> valueType) {
    return getInstance().fromJson(content, valueType);
  }

  /**
   * 将 JSON 字符串反序列化为指定 Type 类型的对象。
   *
   * @param content JSON 字符串
   * @param typeOfT 目标泛型类型
   * @return 反序列化后的对象
   */
  public static <T> T toObject(String content, Type typeOfT) {
    return getInstance().fromJson(content, typeOfT);
  }

  /**
   * 将 JSON 数组字符串反序列化为指定元素类型的 List。
   *
   * @param content JSON 数组字符串
   * @param valueType 列表元素类型
   * @return 反序列化后的 List
   */
  public static <T> List<T> toList(String content, Class<T> valueType) {
    Type type = TypeToken.getParameterized(List.class, valueType).getType();
    return getInstance().fromJson(content, type);
  }

  /**
   * 将 JSON 数组字符串反序列化为 List&lt;Map&lt;String, String&gt;&gt;。
   *
   * @param content JSON 数组字符串
   * @return 反序列化后的 List
   */
  public static <T> List<Map<String, T>> toListMap(String content) {
    return getInstance().fromJson(content, new TypeToken<List<Map<String, String>>>() {}.getType());
  }

  /**
   * 将 JSON 对象字符串反序列化为指定值类型的 Map&lt;String, T&gt;。
   *
   * @param content JSON 对象字符串
   * @param valueType Map 值类型
   * @return 反序列化后的 Map
   */
  public static <T> Map<String, T> toMaps(String content, Class<T> valueType) {
    Type type = TypeToken.getParameterized(Map.class, String.class, valueType).getType();
    return getInstance().fromJson(content, type);
  }
}
