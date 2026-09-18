package tutorials4j.toolkit.json.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.TypeFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Setter
public class JacksonUtils {
  private ObjectMapper objectMapper;

  public static final JacksonUtils instance = new JacksonUtils();

  public <T> String toJson(T domain) {
    return objectMapper.writeValueAsString(domain);
  }

  public <T> JsonNode toNode(T domain) {
    return objectMapper.valueToTree(domain);
  }

  public <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return objectMapper.convertValue(fromValue, toValueType);
  }

  public <T> T treeToValue(TreeNode n, Class<T> valueType) {
    return objectMapper.treeToValue(n, valueType);
  }

  public TypeFactory getTypeFactory() {
    return objectMapper.getTypeFactory();
  }

  public <T> T toObject(String content, Class<T> valueType) {
    return objectMapper.readValue(content, valueType);
  }

  public <T> T toObject(Map<String, Object> content, Class<T> valueType) {
    return objectMapper.convertValue(content, valueType);
  }

  public <T> T toObject(String content, TypeReference<T> typeReference) {
    return objectMapper.readValue(content, typeReference);
  }

  public <T> T toObject(String content, JavaType javaType) {
    return objectMapper.readValue(content, javaType);
  }

  public <T> List<T> toList(String content, Class<T> clazz) {
    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
    return toObject(content, javaType);
  }

  public <K, V> Map<K, V> toMap(String content, Class<K> keyClass, Class<V> valueClass) {
    JavaType javaType =
        objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
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
    return objectMapper.readTree(content);
  }

  public JsonNode toNode(JsonParser jsonParser) {
    return objectMapper.readTree(jsonParser);
  }

  public JsonParser createParser(String content) {
    return objectMapper.createParser(content);
  }
}
