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
  DataStatus getDataStatus();

  /**
   * 设置数据状态。
   *
   * @param status 数据状态
   */
  void setDataStatus(DataStatus status);

  /**
   * 判断状态是否为 {@link DataStatus#NORMAL}。
   *
   * @return true 如果是正常状态
   */
  default boolean isNormal() {
    return DataStatus.NORMAL.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatus#RESERVED}。
   *
   * @return true 如果是保留状态
   */
  default boolean isReserved() {
    return DataStatus.RESERVED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatus#DISABLED}。
   *
   * @return true 如果是禁用状态
   */
  default boolean isDisabled() {
    return DataStatus.DISABLED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatus#LOCKED}。
   *
   * @return true 如果是锁定状态
   */
  default boolean isLocked() {
    return DataStatus.LOCKED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatus#EXPIRED}。
   *
   * @return true 如果是过期状态
   */
  default boolean isExpired() {
    return DataStatus.EXPIRED.equals(getDataStatus());
  }

  /**
   * 判断状态是否为 {@link DataStatus#DELETED}。
   *
   * @return true 如果是已删除状态
   */
  default boolean isDeleted() {
    return DataStatus.DELETED.equals(getDataStatus());
  }
}
