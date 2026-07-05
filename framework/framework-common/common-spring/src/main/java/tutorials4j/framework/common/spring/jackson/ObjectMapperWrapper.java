package tutorials4j.framework.common.spring.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import tutorials4j.framework.common.core.exception.WrapCheckException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ObjectMapperWrapper extends Supplier<ObjectMapper> {

  default <T> String toJson(T domain) {
    try {
      return get().writeValueAsString(domain);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("对象转换字符串异常:" + domain, e);
    }
  }

  default TypeFactory getTypeFactory() {
    return get().getTypeFactory();
  }

  default <T> T toObject(String content, Class<T> valueType) {
    try {
      return get().readValue(content, valueType);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常:" + content, e);
    }
  }

  default <T> T toObject(Map<String, Object> content, Class<T> valueType) {
    try {
      return get().convertValue(content, valueType);
    } catch (IllegalArgumentException e) {
      throw new WrapCheckException("字符串转换对象异常:" + content, e);
    }
  }

  default <T> T toObject(String content, TypeReference<T> typeReference) {
    try {
      return get().readValue(content, typeReference);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常:" + content, e);
    }
  }

  default <T> T toObject(String content, JavaType javaType) {
    try {
      return get().readValue(content, javaType);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }

  default <T> List<T> toList(String content, Class<T> clazz) {
    JavaType javaType = get().getTypeFactory().constructParametricType(List.class, clazz);
    return toObject(content, javaType);
  }

  default <K, V> Map<K, V> toMap(String content, Class<K> keyClass, Class<V> valueClass) {
    JavaType javaType = get().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    return toObject(content, javaType);
  }

  default Map<String, Object> toMap(String content) {
    return toMap(content, String.class, Object.class);
  }

  default <T> Set<T> toSet(String content, Class<T> clazz) {
    JavaType javaType = getTypeFactory().constructCollectionLikeType(Set.class, clazz);
    return toObject(content, javaType);
  }

  default <T> T[] toArray(String content, Class<T> clazz) {
    JavaType javaType = getTypeFactory().constructArrayType(clazz);
    return toObject(content, javaType);
  }

  default <T> T[] toArray(String content) {
    return toObject(content, new TypeReference<T[]>() {});
  }

  default JsonNode toNode(String content) {
    try {
      return get().readTree(content);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常:" + content, e);
    }
  }

  default JsonNode toNode(JsonParser jsonParser) {
    try {
      return get().readTree(jsonParser);
    } catch (IOException e) {
      throw new WrapCheckException("读取数异常", e);
    }
  }

  default JsonParser createParser(String content) {
    try {
      return get().createParser(content);
    } catch (IOException e) {
      throw new WrapCheckException("创建解析器异常", e);
    }
  }
}
