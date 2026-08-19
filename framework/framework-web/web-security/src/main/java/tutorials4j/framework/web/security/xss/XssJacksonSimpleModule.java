package tutorials4j.framework.web.security.xss;

import static tutorials4j.framework.common.core.JacksonConsts.MODULE_ORDER_XSS;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.core.Ordered;
import tutorials4j.framework.common.core.JacksonConsts;

/**
 * Jackson 模块，注册针对 {@code String} 类型的 XSS 清洗反序列化器。
 *
 * <p>该模块将 {@link XssJsonDeserializer} 绑定到所有 {@code String} 类型字段的反序列化过程， 从而在 JSON 解析时自动执行 AntiSamy
 * 清洗，有效防御基于 JSON 传递的跨站脚本攻击。
 *
 * <p>示例：在 Spring Boot 中通过 {@code @Bean} 将此模块添加到 {@code ObjectMapper} 中。
 *
 * @author Yun Jiao
 * @see XssJsonDeserializer
 * @see JacksonConsts
 */
public class XssJacksonSimpleModule extends SimpleModule implements Ordered {
  /** 构造模块并注册 String 类型的 XSS 清洗反序列化器。 */
  public XssJacksonSimpleModule() {
    super(XssJacksonSimpleModule.class.getName(), JacksonConsts.JSON_VERSION);
    this.addDeserializer(String.class, XssJsonDeserializer.instance);
  }

  /** 获取模块的执行顺序。 */
  @Override
  public int getOrder() {
    return MODULE_ORDER_XSS;
  }
}
