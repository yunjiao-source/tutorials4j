package tutorials4j.framework.common.spring.jackson;

import static tutorials4j.framework.common.core.JacksonConsts.MODULE_ORDER_COMMON;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.core.Ordered;
import tutorials4j.framework.common.core.JacksonConsts;

/**
 * @author Yun Jiao
 * @see JacksonConsts
 */
public class CommonSimpleModule extends SimpleModule implements Ordered {
  public CommonSimpleModule() {
    super(CommonSimpleModule.class.getName(), JacksonConsts.JSON_VERSION);
    // {@code Long} 类型序列化为字符串 —— 避免前端 JavaScript 数值溢出问题 前端js数值溢出问题
    this.addSerializer(Long.class, ToStringSerializer.instance);
    this.addSerializer(Long.TYPE, ToStringSerializer.instance);
  }

  @Override
  public int getOrder() {
    return MODULE_ORDER_COMMON;
  }
}
