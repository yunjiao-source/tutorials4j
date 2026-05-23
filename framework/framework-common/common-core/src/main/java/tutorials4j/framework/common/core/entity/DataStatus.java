package tutorials4j.framework.common.core.entity;

/**
 * 数据状态枚举，定义了实体的常见生命周期状态。
 *
 * @author Yun Jiao
 */
public enum DataStatus {
  /** 正常 */
  NORMAL,

  /** 保留 / 留存 */
  RESERVED,

  /** 禁用 */
  DISABLED,

  /** 锁定 */
  LOCKED,

  /** 过期 */
  EXPIRED,

  /** 已删除 / 软删除 */
  DELETED;
}
