package tutorials4j.framework.common.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import tutorials4j.framework.common.core.JacksonConsts;
import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * {@link BaseEnumJsonSerializer} —— 将 {@code BaseEnum} 枚举序列化为包含 code/name 的对象
 *
 * @author Yun Jiao
 * @see BaseEnumJsonSerializer
 * @see JacksonConsts
 */
public class BaseEnumSimpleModule extends SimpleModule {
  public BaseEnumSimpleModule() {
    super(BaseEnumSimpleModule.class.getName(), JacksonConsts.JSON_VERSION);
    this.addSerializer(BaseEnum.class, BaseEnumJsonSerializer.instance);
    this.addDeserializer(BaseEnum.class, BaseEnumJsonDeserializer.instance);
  }
}
