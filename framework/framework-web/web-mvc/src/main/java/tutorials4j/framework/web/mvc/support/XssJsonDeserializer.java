package tutorials4j.framework.web.mvc.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.core.util.XssUtils;

import java.io.IOException;

/**
 * Jackson 自定义反序列化器，用于自动对 JSON 字符串类型的字段进行 XSS 清洗。
 * <p>
 * 当 JSON 中包含字符串值时，该反序列化器会调用 {@link XssUtils#cleaning(String)} 方法，
 * 清除其中的恶意脚本或危险 HTML 标签，从而防止存储或展示时的跨站脚本攻击。
 * </p>
 *
 * @author Yun Jiao
 * @see XssUtils
 */
public class XssJsonDeserializer extends JsonDeserializer<String> {
    public static final XssJsonDeserializer instance = new XssJsonDeserializer();

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();
        if (StringUtils.isNotBlank(value)) {
            return XssUtils.cleaning(value);
        }

        return value;
    }
}
