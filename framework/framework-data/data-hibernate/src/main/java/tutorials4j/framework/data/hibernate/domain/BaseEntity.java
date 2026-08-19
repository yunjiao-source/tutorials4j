package tutorials4j.framework.data.hibernate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tutorials4j.framework.common.core.entity.AuditingEntity;
import tutorials4j.framework.common.core.entity.VersionEntity;

/**
 * JPA 基础实体类，提供通用字段（id、version、创建/修改信息）和自动审计功能。
 *
 * <p>使用雪花算法生成主键，支持乐观锁版本控制，并利用 Spring Data JPA 的审计监听器自动填充时间戳和操作人。
 *
 * @author Yun Jiao
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends BaseIdEntity implements VersionEntity, AuditingEntity {
  /** 乐观锁版本号，用于并发控制。 */
  @Version private Integer version;

  /** 创建时间，插入后不可更新。 */
  @Column(updatable = false)
  @CreatedDate
  private Instant createDate = Instant.now();

  /** 最后修改时间。 */
  @Column @LastModifiedDate private Instant lastModifiedDate = Instant.now();

  /** 创建人。 */
  @Column(length = 36)
  @CreatedBy
  private String createBy;

  /** 最后修改人。 */
  @Column(length = 36)
  @LastModifiedBy
  private String lastModifiedBy;

  /** 获取创建人。 */
  @Override
  public String getCreatedBy() {
    return createBy;
  }

  /** 获取创建时间。 */
  @Override
  public Instant getCreatedDate() {
    return createDate;
  }

  /** 获取最后修改人。 */
  @Override
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /** 获取最后修改时间。 */
  @Override
  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  /** 设置创建人。 */
  @Override
  public void setCreatedBy(String createdBy) {
    this.createBy = createdBy;
  }

  /** 设置创建时间。 */
  @Override
  public void setCreatedDate(Instant createdDate) {
    this.createDate = createdDate;
  }

  /** 设置最后修改人。 */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /** 设置最后修改时间。 */
  @Override
  public void setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  /** 获取乐观锁版本号。 */
  @Override
  public Integer getVersion() {
    return version;
  }

  /** 设置乐观锁版本号。 */
  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }
}
