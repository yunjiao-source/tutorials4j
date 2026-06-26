package tutorials4j.framework.common.core.bean;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum YesNoEnum implements BaseEnum<Integer> {
  N(0, "否"),
  Y(1, "是");

  private final int code;
  private final String label;

  YesNoEnum(int code, String label) {
    this.code = code;
    this.label = label;
  }

  @Override
  public Integer getCode() {
    return code;
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public String getLabel() {
    return label;
  }
}
