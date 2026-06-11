package tutorials4j.framework.common.core.entity;

import tutorials4j.framework.common.core.bean.BaseEnum;
import tutorials4j.framework.common.core.support.EnumCache;

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

  static {
    EnumCache.registerByName(DataStatusEnum.class, DataStatusEnum.values());
    EnumCache.registerByValue(
        DataStatusEnum.class, DataStatusEnum.values(), DataStatusEnum::getCode);
  }
}
