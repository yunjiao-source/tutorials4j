package tutorials4j.framework.data.core.support;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.common.core.util.GsonUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Converter
public class ListConverter implements AttributeConverter<List<String>, String> {

  @Override
  public String convertToDatabaseColumn(List<String> object) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    return GsonUtils.toJson(object);
  }

  @Override
  public List<String> convertToEntityAttribute(String json) {
    if (ObjectUtils.isEmpty(json)) {
      return Collections.emptyList();
    }
    return GsonUtils.toList(json, String.class);
  }
}
