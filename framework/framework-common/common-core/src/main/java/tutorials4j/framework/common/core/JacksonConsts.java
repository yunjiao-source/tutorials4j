package tutorials4j.framework.common.core;

import com.fasterxml.jackson.core.Version;

/**
 * JSON 处理相关的常量定义。
 *
 * @author Yun Jiao
 */
public interface JacksonConsts {
  /** 当前 JSON 处理组件所基于的 Jackson 版本。 */
  Version JSON_VERSION = new Version(2, 21, 2, null, null, null);

  /** Jackson 模块默认排序值 */
  int MODULE_ORDER_DEFAULT = 100;

  /** 公共模块排序值 */
  int MODULE_ORDER_COMMON = MODULE_ORDER_DEFAULT + 10;

  /** XSS 模块排序值 */
  int MODULE_ORDER_XSS = MODULE_ORDER_COMMON + 10;
}
