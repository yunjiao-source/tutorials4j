package tutorials4j.framework.common.core.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * Gson工具
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

  public static JsonElement toJsonElement(String content) {
    return JsonParser.parseString(content);
  }

  public static JsonArray toJsonArray(String content) {
    return toJsonElement(content).getAsJsonArray();
  }

  public static JsonObject toJsonObject(String content) {
    return toJsonElement(content).getAsJsonObject();
  }

  public static <T> String toJson(T domain) {
    return getInstance().toJson(domain);
  }

  public static <T> T toObject(String content, Class<T> valueType) {
    return getInstance().fromJson(content, valueType);
  }

  public static <T> T toObject(String content, Type typeOfT) {
    return getInstance().fromJson(content, typeOfT);
  }

  public static <T> List<T> toList(String content, Class<T> valueType) {
    Type type = TypeToken.getParameterized(List.class, valueType).getType();
    return getInstance().fromJson(content, type);
  }

  public static <T> List<Map<String, T>> toListMap(String content) {
    return getInstance().fromJson(content, new TypeToken<List<Map<String, String>>>() {}.getType());
  }

  public static <T> Map<String, T> toMaps(String content, Class<T> valueType) {
    Type type = TypeToken.getParameterized(Map.class, String.class, valueType).getType();
    return getInstance().fromJson(content, type);
  }
}
