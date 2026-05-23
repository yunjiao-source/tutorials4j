package tutorials4j.framework.common.core.entity;

import java.time.LocalDateTime;

/**
 * 审计实体接口，提供创建和修改信息的记录能力。
 *
 * <p>实现了该接口的实体可以记录创建人、创建时间、最后修改人和最后修改时间。
 *
 * @author Yun Jiao
 */
public interface AuditingEntity extends Entity {

  /**
   * 获取创建人标识。
   *
   * @return 创建人
   */
  String getCreatedBy();

  /**
   * 获取创建时间。
   *
   * @return 创建时间
   */
  LocalDateTime getCreatedDate();

  /**
   * 获取最后修改人标识。
   *
   * @return 最后修改人
   */
  String getLastModifiedBy();

  /**
   * 获取最后修改时间。
   *
   * @return 最后修改时间
   */
  LocalDateTime getLastModifiedDate();

  /**
   * 设置创建人标识。
   *
   * @param createdBy 创建人
   */
  void setCreatedBy(String createdBy);

  /**
   * 设置创建时间。
   *
   * @param createdDate 创建时间
   */
  void setCreatedDate(LocalDateTime createdDate);

  /**
   * 设置最后修改人标识。
   *
   * @param lastModifiedBy 最后修改人
   */
  void setLastModifiedBy(String lastModifiedBy);

  /**
   * 设置最后修改时间。
   *
   * @param lastModifiedDate 最后修改时间
   */
  void setLastModifiedDate(LocalDateTime lastModifiedDate);
}
