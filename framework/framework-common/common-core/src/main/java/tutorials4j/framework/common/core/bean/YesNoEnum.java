package tutorials4j.framework.common.core.bean;

/**
 * 是否枚举，表示是（Y）与否（N）两种状态，编码值分别为 1 与 0。
 *
 * @author Yun Jiao
 */
public enum YesNoEnum implements BaseEnum<Integer> {
  /** 否 */
  N(0, "否"),
  /** 是 */
  Y(1, "是");

  private final int code;
  private final String label;

  YesNoEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  /**
   * 返回该枚举项对应的编码值。
   *
   * @return 编码值（0 表示否，1 表示是）
   */
  @Override
  public Integer getCode() {
    return code;
  }

  /**
   * 返回该枚举项的枚举名称。
   *
   * @return 枚举名称字符串
   */
  @Override
  public String getName() {
    return name();
  }

  /**
   * 返回该枚举项的标签（可读的中文描述）。
   *
   * @return 标签字符串
   */
  @Override
  public String getLabel() {
    return label;
  }
}
