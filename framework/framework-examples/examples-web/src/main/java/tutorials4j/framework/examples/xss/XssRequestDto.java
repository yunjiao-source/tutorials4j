// XssRequestDto.java
package tutorials4j.framework.examples.xss;

import lombok.Data;

/**
 * XSS 清洗演示的请求体对象。
 *
 * <p>承载需要验证清洗效果的请求内容字段。
 *
 * @author Yun Jiao
 */
@Data
public class XssRequestDto {
  /** 请求内容，反序列化时会被 XssJsonDeserializer 清洗。 */
  private String content;
}
