package tutorials4j.framework.data.hibernate.domain;

import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import tutorials4j.framework.common.core.bean.DataStatusEnum;
import tutorials4j.framework.common.core.entity.StatusEntity;
import tutorials4j.framework.data.core.exception.DataErrorCode;

/**
 * 带数据状态的基础实体类，扩展自 {@link BaseEntity} 并实现 {@link StatusEntity}。
 *
 * <p>在持久化前自动将状态初始化为 {@link DataStatusEnum#NORMAL}，在删除前如果状态为 {@link DataStatusEnum#RESERVED} 则抛出异常。
 *
 * @author Yun Jiao
 */
@MappedSuperclass
public class BaseStatusEntity extends BaseEntity implements StatusEntity {

  @Convert(converter = DataStatusAttributeConverter.class)
  private DataStatusEnum dataStatus;

  @Override
  public DataStatusEnum getDataStatus() {
    return dataStatus;
  }

  @Override
  public void setDataStatus(DataStatusEnum status) {
    this.dataStatus = status;
  }

  /** 持久化前回调，自动设置默认状态。 */
  @PrePersist
  public void prePersist() {
    if (dataStatus == null) {
      dataStatus = DataStatusEnum.NORMAL;
    }
  }

  /**
   * 删除前回调，禁止删除保留状态的数据。
   *
   * @throws RuntimeException 如果状态为 {@link DataStatusEnum#RESERVED}
   */
  @PreRemove
  public void preRemove() {
    if (isReserved()) {
      throw DataErrorCode.DATA_ENTITY_RESERVED_CANNT_REMOVE.throwed();
    }
  }
}
