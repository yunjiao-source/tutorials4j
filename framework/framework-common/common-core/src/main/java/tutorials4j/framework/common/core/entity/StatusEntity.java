package tutorials4j.framework.common.core.entity;

import tutorials4j.framework.common.core.bean.DataStatusEnum;

/**
 * 具有数据状态的实体接口，提供数据状态的存取能力以及便捷的状态判断方法。
 *
 * @author Yun Jiao
 */
public interface StatusEntity extends Entity {
  /**
   * 获取当前数据状态。
   *
   * @return 数据状态
   */
  DataStatusEnum getDataStatus();

  /**
   * 设置数据状态。
   *
   * @param dataStatus 数据状态
   */
  void setDataStatus(DataStatusEnum dataStatus);

  /**
   * 判断当前数据状态是否为 {@link DataStatusEnum#NORMAL}。
   *
   * @return true 表示状态为正常，否则返回 false
   */
  default boolean isNormal() {
    return DataStatusEnum.NORMAL.equals(getDataStatus());
  }

  /**
   * 判断当前数据状态是否为 {@link DataStatusEnum#RESERVED}。
   *
   * @return true 表示状态为保留，否则返回 false
   */
  default boolean isReserved() {
    return DataStatusEnum.RESERVED.equals(getDataStatus());
  }

  /**
   * 判断当前数据状态是否为 {@link DataStatusEnum#DISABLED}。
   *
   * @return true 表示状态为禁用，否则返回 false
   */
  default boolean isDisabled() {
    return DataStatusEnum.DISABLED.equals(getDataStatus());
  }

  /**
   * 判断当前数据状态是否为 {@link DataStatusEnum#LOCKED}。
   *
   * @return true 表示状态为锁定，否则返回 false
   */
  default boolean isLocked() {
    return DataStatusEnum.LOCKED.equals(getDataStatus());
  }

  /**
   * 判断当前数据状态是否为 {@link DataStatusEnum#EXPIRED}。
   *
   * @return true 表示状态为过期，否则返回 false
   */
  default boolean isExpired() {
    return DataStatusEnum.EXPIRED.equals(getDataStatus());
  }

  /**
   * 判断当前数据状态是否为 {@link DataStatusEnum#DELETED}。
   *
   * @return true 表示状态为已删除，否则返回 false
   */
  default boolean isDeleted() {
    return DataStatusEnum.DELETED.equals(getDataStatus());
  }
}
