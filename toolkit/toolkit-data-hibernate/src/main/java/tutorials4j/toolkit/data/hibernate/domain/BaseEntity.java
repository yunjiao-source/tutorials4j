package tutorials4j.toolkit.data.hibernate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tutorials4j.toolkit.data.domain.AuditingEntity;
import tutorials4j.toolkit.data.domain.VersionEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity<ID extends Serializable> extends BaseIdEntity<ID>
    implements VersionEntity, AuditingEntity {
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
