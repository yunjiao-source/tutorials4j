package tutorials4j.framework.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * Jackson 序列化器，用于将 {@link BaseEnum} 实例序列化为包含 "code" 和 "name" 字段的 JSON 对象。
 *
 * <p>序列化后的 JSON 格式示例：
 *
 * <pre>{@code
 * {
 *   "code": 0,
 *   "name": "禁用"
 * }
 * }</pre>
 *
 * 该类提供静态单例实例 {@link #instance}，便于直接注册到 Jackson 模块中。
 *
 * @author Yun Jiao
 * @see BaseEnumSimpleModule
 */
public class BaseEnumJsonSerializer extends JsonSerializer<BaseEnum<?>> {
  public static final BaseEnumJsonSerializer instance = new BaseEnumJsonSerializer();

  @Override
  public void serialize(BaseEnum<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (value == null) {
      gen.writeNull();
      return;
    }

    gen.writeStartObject();
    // 输出 code
    gen.writeObjectField("code", value.getCode());
    gen.writeObjectField("name", value.getName());

    gen.writeEndObject();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<BaseEnum<?>> handledType() {
    return (Class<BaseEnum<?>>) (Class<?>) BaseEnum.class;
  }
}
