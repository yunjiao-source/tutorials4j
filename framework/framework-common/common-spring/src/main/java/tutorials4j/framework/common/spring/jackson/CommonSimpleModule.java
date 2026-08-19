package tutorials4j.framework.common.spring.jackson;

import static tutorials4j.framework.common.core.JacksonConsts.MODULE_ORDER_COMMON;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.core.Ordered;
import tutorials4j.framework.common.core.JacksonConsts;

/**
 * Jackson 公共序列化模块，注册 Long 类型转字符串的序列化器。
 *
 * <p>将 {@link Long} 及其基本类型 {@code long} 统一序列化为字符串，避免前端 JavaScript 处理大整数时出现精度溢出的问题；实现 {@link
 * Ordered} 以控制模块的加载顺序。
 *
 * @author Yun Jiao
 * @see JacksonConsts
 */
public class CommonSimpleModule extends SimpleModule implements Ordered {
  /** 构造公共序列化模块，并注册 Long 类型转字符串的序列化器。 */
  public CommonSimpleModule() {
    super(CommonSimpleModule.class.getName(), JacksonConsts.JSON_VERSION);
    // {@code Long} 类型序列化为字符串 —— 避免前端 JavaScript 数值溢出问题 前端js数值溢出问题
    this.addSerializer(Long.class, ToStringSerializer.instance);
    this.addSerializer(Long.TYPE, ToStringSerializer.instance);
  }

  /**
   * 返回模块的加载顺序。
   *
   * @return 模块顺序值 {@link JacksonConsts#MODULE_ORDER_COMMON}
   */
  @Override
  public int getOrder() {
    return MODULE_ORDER_COMMON;
  }
}
