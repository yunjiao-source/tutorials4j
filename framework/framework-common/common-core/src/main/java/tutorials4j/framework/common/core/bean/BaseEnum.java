package tutorials4j.framework.common.core.bean;

/**
 * 枚举类型的基础接口，定义了枚举项应提供的标准访问方法。
 *
 * <p>实现该接口的枚举类可用于统一处理 code（编码）和 name（名称）， 便于在序列化、持久化等场景中基于编码进行转换。
 *
 * @param <T> 编码的数据类型，如 Integer、String 等
 * @author Yun Jiao
 * @see tutorials4j.framework.common.core.json.BaseEnumJsonSerializer
 */
public interface BaseEnum<T> {
  /**
   * 获取枚举项的编码值。
   *
   * @return 编码值，类型为 T
   */
  T getCode();

  /**
   * 获取枚举项的名称（通常为可读的文本描述）。
   *
   * @return 名称字符串
   */
  String getName();
}
