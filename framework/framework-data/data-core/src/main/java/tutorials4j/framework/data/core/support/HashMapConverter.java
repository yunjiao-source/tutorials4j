package tutorials4j.framework.data.core.support;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.common.core.util.GsonUtils;

import java.util.Collections;
import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Converter
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> object) {
        if (ObjectUtils.isEmpty(object)) {
            return null;
        }
        return GsonUtils.toJson(object);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String json) {
        if (ObjectUtils.isEmpty(json)) {
            return Collections.emptyMap();
        }
        return GsonUtils.toMaps(json, Object.class);
    }
}
