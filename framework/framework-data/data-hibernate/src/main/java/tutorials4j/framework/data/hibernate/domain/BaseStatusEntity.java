package tutorials4j.framework.data.hibernate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import tutorials4j.framework.common.core.entity.DataStatus;
import tutorials4j.framework.common.core.entity.StatusEntity;

/**
 * 带数据状态的基础实体类，扩展自 {@link BaseEntity} 并实现 {@link StatusEntity}。
 *
 * <p>在持久化前自动将状态初始化为 {@link DataStatus#NORMAL}，在删除前如果状态为 {@link DataStatus#RESERVED} 则抛出异常。
 *
 * @author Yun Jiao
 */
@MappedSuperclass
public class BaseStatusEntity extends BaseEntity implements StatusEntity {
  @Column(length = 36)
  @Enumerated(EnumType.STRING)
  private DataStatus status;

  @Override
  public DataStatus getDataStatus() {
    return status;
  }

  @Override
  public void setDataStatus(DataStatus status) {
    this.status = status;
  }

  /** 持久化前回调，自动设置默认状态。 */
  @PrePersist
  public void prePersist() {
    if (status == null) {
      status = DataStatus.NORMAL;
    }
  }

  /**
   * 删除前回调，禁止删除保留状态的数据。
   *
   * @throws RuntimeException 如果状态为 {@link DataStatus#RESERVED}
   */
  @PreRemove
  public void preRemove() {
    if (isReserved()) {
      throw new RuntimeException("保留数据，不能删除");
    }
  }
}
