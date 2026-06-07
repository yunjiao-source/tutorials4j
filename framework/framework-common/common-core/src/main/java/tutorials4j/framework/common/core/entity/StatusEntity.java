package tutorials4j.framework.common.core.entity;

/**
 * 具有数据状态的实体接口，提供状态管理和便捷的状态判断方法。
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
   * 判断状态是否为 {@link DataStatusEnum#NORMAL}。
   *
   * @return true 如果是正常状态
   */
  default boolean isNormal() {
    return DataStatusEnum.NORMAL.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatusEnum#RESERVED}。
   *
   * @return true 如果是保留状态
   */
  default boolean isReserved() {
    return DataStatusEnum.RESERVED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatusEnum#DISABLED}。
   *
   * @return true 如果是禁用状态
   */
  default boolean isDisabled() {
    return DataStatusEnum.DISABLED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatusEnum#LOCKED}。
   *
   * @return true 如果是锁定状态
   */
  default boolean isLocked() {
    return DataStatusEnum.LOCKED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatusEnum#EXPIRED}。
   *
   * @return true 如果是过期状态
   */
  default boolean isExpired() {
    return DataStatusEnum.EXPIRED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatusEnum#DELETED}。
   *
   * @return true 如果是已删除状态
   */
  default boolean isDeleted() {
    return DataStatusEnum.DELETED.equals(getDataStatus());
  }
}
