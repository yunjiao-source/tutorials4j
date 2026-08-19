package tutorials4j.framework.common.core.bean;

/**
 * 数据状态枚举，定义了实体的常见生命周期状态。
 *
 * @author Yun Jiao
 */
public enum DataStatusEnum implements BaseEnum<Integer> {
  /** 正常 */
  NORMAL(1, "正常"),

  /** 保留 / 留存 */
  RESERVED(2, "留存"),

  /** 禁用 */
  DISABLED(3, "禁用"),

  /** 锁定 */
  LOCKED(4, "锁定"),

  /** 过期 */
  EXPIRED(5, "过期"),

  /** 已删除 / 软删除 */
  DELETED(6, "已删除");

  private final Integer code;
  private final String label;

  DataStatusEnum(Integer code, String label) {
    this.code = code;
    this.label = label;
  }

  /**
   * 返回该状态对应的编码值。
   *
   * @return 编码值
   */
  @Override
  public Integer getCode() {
    return code;
  }

  /**
   * 返回该状态的枚举名称。
   *
   * @return 枚举名称字符串
   */
  @Override
  public String getName() {
    return name();
  }

  /**
   * 返回该状态的标签（可读的中文描述）。
   *
   * @return 状态标签字符串
   */
  @Override
  public String getLabel() {
    return label;
  }
}
