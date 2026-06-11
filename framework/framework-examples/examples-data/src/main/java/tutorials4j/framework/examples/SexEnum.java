package tutorials4j.framework.examples;

import lombok.Getter;
import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * 性别
 *
 * @author Yun Jiao
 */
@Getter
public enum SexEnum implements BaseEnum<String> {
  male("nan", "男"),
  female("nv", "女");

  private final String code;
  private final String label;

  SexEnum(String code, String label) {
    this.code = code;
    this.label = label;
  }

  @Override
  public String getCode() {
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
