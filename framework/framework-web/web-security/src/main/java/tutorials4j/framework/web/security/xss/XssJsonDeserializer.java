package tutorials4j.framework.web.security.xss;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.framework.common.spring.util.XssUtils;

/**
 * Jackson 自定义反序列化器，用于自动对 JSON 字符串类型的字段进行 XSS 清洗。
 *
 * <p>当 JSON 中包含字符串值时，该反序列化器会调用 {@link XssUtils#cleaning(String)} 方法， 清除其中的恶意脚本或危险 HTML
 * 标签，从而防止存储或展示时的跨站脚本攻击。
 *
 * @author Yun Jiao
 * @see XssUtils
 */
public class XssJsonDeserializer extends JsonDeserializer<String> {
  /** 反序列化器单例实例 */
  public static final XssJsonDeserializer instance = new XssJsonDeserializer();

  /** 声明该反序列化器处理的类型为 String。 */
  @Override
  public Class<String> handledType() {
    return String.class;
  }

  /**
   * 反序列化字符串值并执行 XSS 清洗。
   *
   * @param jsonParser JSON 解析器
   * @param deserializationContext 反序列化上下文
   * @return 清洗后的字符串值
   * @throws IOException 读取 JSON 值失败时抛出
   * @throws JsonProcessingException JSON 解析失败时抛出
   */
  @Override
  public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JsonProcessingException {
    String value = jsonParser.getValueAsString();
    if (StringUtils.isNotBlank(value)) {
      return XssUtils.cleaning(value);
    }

    return value;
  }
}
