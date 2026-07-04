package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.framework.common.core.exception.WrapCheckException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class Jackson2Utils {
  public static final Jackson2Utils instance = new Jackson2Utils();

  private ObjectMapper objectMapper;

  public <T> String toJson(T domain) {
    try {
      return getObjectMapper().writeValueAsString(domain);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public TypeFactory getTypeFactory() {
    return getObjectMapper().getTypeFactory();
  }

  public <T> T toObject(String content, Class<T> valueType) {
    try {
      return getObjectMapper().readValue(content, valueType);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public <T> T toObject(Map<String, Object> content, Class<T> valueType) {
    try {
      return getObjectMapper().convertValue(content, valueType);
    } catch (IllegalArgumentException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public <T> T toObject(String content, TypeReference<T> typeReference) {
    try {
      return getObjectMapper().readValue(content, typeReference);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public <T> T toObject(String content, JavaType javaType) {
    try {
      return getObjectMapper().readValue(content, javaType);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public <T> List<T> toList(String content, Class<T> clazz) {
    JavaType javaType =
        getObjectMapper().getTypeFactory().constructParametricType(List.class, clazz);
    return toObject(content, javaType);
  }

  public <K, V> Map<K, V> toMap(String content, Class<K> keyClass, Class<V> valueClass) {
    JavaType javaType =
        getObjectMapper().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    return toObject(content, javaType);
  }

  public Map<String, Object> toMap(String content) {
    return toMap(content, String.class, Object.class);
  }

  public <T> Set<T> toSet(String content, Class<T> clazz) {
    JavaType javaType = getTypeFactory().constructCollectionLikeType(Set.class, clazz);
    return toObject(content, javaType);
  }

  public <T> T[] toArray(String content, Class<T> clazz) {
    JavaType javaType = getTypeFactory().constructArrayType(clazz);
    return toObject(content, javaType);
  }

  public <T> T[] toArray(String content) {
    return toObject(content, new TypeReference<T[]>() {});
  }

  public JsonNode toNode(String content) {
    try {
      return getObjectMapper().readTree(content);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public JsonNode toNode(JsonParser jsonParser) {
    try {
      return getObjectMapper().readTree(jsonParser);
    } catch (IOException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  public JsonParser createParser(String content) {
    try {
      return getObjectMapper().createParser(content);
    } catch (IOException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  @SuppressWarnings("deprecation")
  public <R> R loop(JsonNode jsonNode, Function<JsonNode, R> function) {
    if (jsonNode.isObject()) {
      Iterator<Entry<String, JsonNode>> it = jsonNode.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> entry = it.next();
        loop(entry.getValue(), function);
      }
    }

    if (jsonNode.isArray()) {
      for (JsonNode node : jsonNode) {
        loop(node, function);
      }
    }

    if (jsonNode.isValueNode()) {
      return function.apply(jsonNode);
    } else {
      return null;
    }
  }
}
