package tutorials4j.framework.data.core.support;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * Map 与 JSON 字符串之间的 JPA 属性转换器。
 *
 * <p>用于将实体的 {@code Map<String, Object>} 字段以 JSON 字符串形式持久化到数据库， 读取时再反序列化还原，空值场景下分别返回 null 与空 Map。
 *
 * @author Yun Jiao
 */
@Converter
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

  /**
   * 将 Map 转换为 JSON 字符串存入数据库。
   *
   * @param object 待转换的 Map 数据
   * @return JSON 字符串；Map 为空时返回 null
   */
  @Override
  public String convertToDatabaseColumn(Map<String, Object> object) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    return GsonUtils.toJson(object);
  }

  /**
   * 将数据库中的 JSON 字符串还原为 Map。
   *
   * @param json 数据库存储的 JSON 字符串
   * @return 还原后的 Map；JSON 为空时返回空 Map
   */
  @Override
  public Map<String, Object> convertToEntityAttribute(String json) {
    if (ObjectUtils.isEmpty(json)) {
      return Collections.emptyMap();
    }
    return GsonUtils.toMaps(json, Object.class);
  }
}
