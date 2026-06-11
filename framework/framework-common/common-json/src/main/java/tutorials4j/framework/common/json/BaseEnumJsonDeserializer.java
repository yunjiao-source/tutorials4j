package tutorials4j.framework.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import tutorials4j.framework.common.core.bean.BaseEnum;
import tutorials4j.framework.common.core.support.EnumCache;

/**
 * Jackson 反序列化器，用于将包含 "code" 的 JSON 对象还原为 {@link BaseEnum} 实例。
 *
 * <p>支持的 JSON 格式示例：
 *
 * <pre>{@code
 * {
 *   "code": 0,
 *   "name": "禁用",
 *   "label": "禁用"
 * }
 * }</pre>
 *
 * 其中 "name" 和 "label" 字段在反序列化时会被忽略，仅依赖 "code" 定位枚举。
 *
 * @author Yun Jiao
 * @see BaseEnumSimpleModule
 */
public class BaseEnumJsonDeserializer extends JsonDeserializer<BaseEnum<?>> {

  public static final BaseEnumJsonDeserializer instance = new BaseEnumJsonDeserializer();

  @Override
  public BaseEnum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    // 处理 JSON 中的 null 值
    if (p.currentToken() == JsonToken.VALUE_NULL) {
      return null;
    }

    JavaType currentType = ctxt.getContextualType();
    Class<?> rawClass = currentType.getRawClass();

    if (!rawClass.isEnum() || !BaseEnum.class.isAssignableFrom(rawClass)) {
      throw new IllegalArgumentException(
          "Type " + rawClass + " is not an enum implementing BaseEnum");
    }
    JsonNode node = p.readValueAsTree();
    JsonNode codeNode = node.get("code");
    if (codeNode == null) {
      // 没有 code 字段，无法反序列化
      return (BaseEnum<?>) ctxt.handleUnexpectedToken(BaseEnum.class, p);
    }

    // 获取 code 值（支持 Number 或 String）
    Object codeValue;
    if (codeNode.isNumber()) {
      codeValue = codeNode.numberValue();
    } else if (codeNode.isTextual()) {
      codeValue = codeNode.textValue();
    } else {
      return (BaseEnum<?>) ctxt.handleUnexpectedToken(BaseEnum.class, p);
    }

    BaseEnum<?> result = EnumCache.findByValue(rawClass, codeValue);
    if (result == null
        && !ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
      return (BaseEnum<?>)
          ctxt.handleWeirdKey(
              BaseEnum.class, String.valueOf(codeValue), "No enum constant with code " + codeValue);
    }
    return result;
  }

  @Override
  public Class<?> handledType() {
    return BaseEnum.class;
  }
}
