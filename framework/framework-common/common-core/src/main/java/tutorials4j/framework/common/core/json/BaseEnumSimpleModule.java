package tutorials4j.framework.common.core.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * {@link BaseEnumJsonSerializer} —— 将 {@code BaseEnum} 枚举序列化为包含 code/name 的对象
 *
 * @author Yun Jiao
 * @see BaseEnumJsonSerializer
 * @see JsonConsts
 */
public class BaseEnumSimpleModule extends SimpleModule {
  public BaseEnumSimpleModule() {
    super(BaseEnumSimpleModule.class.getName(), JsonConsts.JSON_VERSION);
    this.addSerializer(BaseEnumJsonSerializer.instance);
  }
}
