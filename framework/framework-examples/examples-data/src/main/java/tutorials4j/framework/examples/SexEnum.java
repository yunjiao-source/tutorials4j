package tutorials4j.framework.examples;

import lombok.Getter;
import tutorials4j.framework.common.core.bean.BaseEnum;

/**
 * 性别枚举。
 *
 * <p>实现 {@link BaseEnum}，为性别提供稳定的字符串编码（{@code nan}/{@code nv}）与 中文标签（男/女），便于在数据库中按编码存储。
 *
 * @author Yun Jiao
 */
@Getter
public enum SexEnum implements BaseEnum<String> {
  /** 男性，编码 {@code nan} */
  male("nan", "男"),
  /** 女性，编码 {@code nv} */
  female("nv", "女");

  /** 性别编码 */
  private final String code;

  /** 性别中文标签 */
  private final String label;

  /**
   * 构造性别枚举项。
   *
   * @param code 性别编码
   * @param label 性别中文标签
   */
  SexEnum(String code, String label) {
    this.code = code;
    this.label = label;
  }

  /** 返回性别编码。 */
  @Override
  public String getCode() {
    return code;
  }

  /** 返回枚举名称。 */
  @Override
  public String getName() {
    return name();
  }

  /** 返回性别中文标签。 */
  @Override
  public String getLabel() {
    return label;
  }
}
