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
import tutorials4j.framework.common.core.exception.BaseErrorCode;

/**
 * ObjectMapper 包装接口，统一提供 JSON 序列化与反序列化工具方法。
 *
 * <p>继承 {@link Supplier}{@code <ObjectMapper>}，由实现类提供实际的 ObjectMapper 实例； 各默认方法在此基础上封装了对象与 JSON
 * 字符串、Map、集合、数组及 {@link JsonNode} 之间的转换能力。
 *
 * @author Yun Jiao
 */
public interface ObjectMapperWrapper extends Supplier<ObjectMapper> {

  /**
   * 将对象序列化为 JSON 字符串。
   *
   * @param domain 待序列化的对象
   * @param <T> 对象类型
   * @return JSON 字符串
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 序列化失败时抛出
   */
  default <T> String toJson(T domain) {
    try {
      return get().writeValueAsString(domain);
    } catch (JsonProcessingException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("对象转换字符串异常", e).param("domain", domain);
    }
  }

  /**
   * 获取底层 ObjectMapper 的类型工厂。
   *
   * @return {@link TypeFactory} 实例
   */
  default TypeFactory getTypeFactory() {
    return get().getTypeFactory();
  }

  /**
   * 将 JSON 字符串转换为指定类型的对象。
   *
   * @param content JSON 字符串
   * @param valueType 目标类型
   * @param <T> 目标类型
   * @return 转换后的对象
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 转换失败时抛出
   */
  default <T> T toObject(String content, Class<T> valueType) {
    try {
      return get().readValue(content, valueType);
    } catch (JsonProcessingException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("字符串转换对象异常", e).param("content", content);
    }
  }

  /**
   * 将 Map 转换为指定类型的对象。
   *
   * @param content 源 Map
   * @param valueType 目标类型
   * @param <T> 目标类型
   * @return 转换后的对象
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 转换失败时抛出
   */
  default <T> T toObject(Map<String, Object> content, Class<T> valueType) {
    try {
      return get().convertValue(content, valueType);
    } catch (IllegalArgumentException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("字符串转换对象异常", e).param("content", content);
    }
  }

  /**
   * 将 JSON 字符串转换为指定泛型类型的对象。
   *
   * @param content JSON 字符串
   * @param typeReference 泛型类型引用
   * @param <T> 目标类型
   * @return 转换后的对象
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 转换失败时抛出
   */
  default <T> T toObject(String content, TypeReference<T> typeReference) {
    try {
      return get().readValue(content, typeReference);
    } catch (JsonProcessingException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("字符串转换对象异常", e).param("content", content);
    }
  }

  /**
   * 将 JSON 字符串转换为指定 JavaType 类型的对象。
   *
   * @param content JSON 字符串
   * @param javaType 目标 JavaType
   * @param <T> 目标类型
   * @return 转换后的对象
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 转换失败时抛出
   */
  default <T> T toObject(String content, JavaType javaType) {
    try {
      return get().readValue(content, javaType);
    } catch (JsonProcessingException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("字符串转换对象异常", e);
    }
  }

  /**
   * 将 JSON 字符串转换为指定元素类型的 List。
   *
   * @param content JSON 字符串
   * @param clazz 元素类型
   * @param <T> 元素类型
   * @return 转换后的 List
   */
  default <T> List<T> toList(String content, Class<T> clazz) {
    JavaType javaType = get().getTypeFactory().constructParametricType(List.class, clazz);
    return toObject(content, javaType);
  }

  /**
   * 将 JSON 字符串转换为指定键值类型的 Map。
   *
   * @param content JSON 字符串
   * @param keyClass 键类型
   * @param valueClass 值类型
   * @param <K> 键类型
   * @param <V> 值类型
   * @return 转换后的 Map
   */
  default <K, V> Map<K, V> toMap(String content, Class<K> keyClass, Class<V> valueClass) {
    JavaType javaType = get().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    return toObject(content, javaType);
  }

  /**
   * 将 JSON 字符串转换为 {@code Map<String, Object>}。
   *
   * @param content JSON 字符串
   * @return 转换后的 Map
   */
  default Map<String, Object> toMap(String content) {
    return toMap(content, String.class, Object.class);
  }

  /**
   * 将 JSON 字符串转换为指定元素类型的 Set。
   *
   * @param content JSON 字符串
   * @param clazz 元素类型
   * @param <T> 元素类型
   * @return 转换后的 Set
   */
  default <T> Set<T> toSet(String content, Class<T> clazz) {
    JavaType javaType = getTypeFactory().constructCollectionLikeType(Set.class, clazz);
    return toObject(content, javaType);
  }

  /**
   * 将 JSON 字符串转换为指定元素类型的数组。
   *
   * @param content JSON 字符串
   * @param clazz 元素类型
   * @param <T> 元素类型
   * @return 转换后的数组
   */
  default <T> T[] toArray(String content, Class<T> clazz) {
    JavaType javaType = getTypeFactory().constructArrayType(clazz);
    return toObject(content, javaType);
  }

  /**
   * 将 JSON 字符串转换为指定泛型类型的数组。
   *
   * @param content JSON 字符串
   * @param <T> 元素类型
   * @return 转换后的数组
   */
  default <T> T[] toArray(String content) {
    return toObject(content, new TypeReference<T[]>() {});
  }

  /**
   * 将 JSON 字符串解析为 {@link JsonNode}。
   *
   * @param content JSON 字符串
   * @return 解析后的 JsonNode
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 解析失败时抛出
   */
  default JsonNode toNode(String content) {
    try {
      return get().readTree(content);
    } catch (JsonProcessingException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("字符串转换对象异常", e).param("content", content);
    }
  }

  /**
   * 从 {@link JsonParser} 解析 {@link JsonNode} 树。
   *
   * @param jsonParser JSON 解析器
   * @return 解析后的 JsonNode
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 解析失败时抛出
   */
  default JsonNode toNode(JsonParser jsonParser) {
    try {
      return get().readTree(jsonParser);
    } catch (IOException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("读取树异常", e);
    }
  }

  /**
   * 为指定内容创建 JSON 解析器。
   *
   * @param content JSON 字符串
   * @return JsonParser 实例
   * @throws tutorials4j.framework.common.core.exception.ErrorCodeException 创建失败时抛出
   */
  default JsonParser createParser(String content) {
    try {
      return get().createParser(content);
    } catch (IOException e) {
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed("创建解析器异常", e);
    }
  }
}
