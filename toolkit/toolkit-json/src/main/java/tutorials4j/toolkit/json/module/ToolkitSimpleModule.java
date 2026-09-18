package tutorials4j.toolkit.json.module;

import org.springframework.core.Ordered;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ToolkitSimpleModule extends SimpleModule implements Ordered {
  public ToolkitSimpleModule() {
    super(ToolkitSimpleModule.class.getName());
    // {@code Long} 类型序列化为字符串 —— 避免前端 JavaScript 数值溢出问题 前端js数值溢出问题
    this.addSerializer(Long.class, ToStringSerializer.instance);
    this.addSerializer(Long.TYPE, ToStringSerializer.instance);
  }

  @Override
  public int getOrder() {
    return 100;
  }
}
