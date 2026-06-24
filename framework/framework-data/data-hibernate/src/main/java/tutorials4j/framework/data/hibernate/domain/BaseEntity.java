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
  @Version private Integer version;

  @Column(updatable = false)
  @CreatedDate
  private Instant createDate = Instant.now();

  @Column @LastModifiedDate private Instant lastModifiedDate = Instant.now();

  @Column(length = 36)
  @CreatedBy
  private String createBy;

  @Column(length = 36)
  @LastModifiedBy
  private String lastModifiedBy;

  @Override
  public String getCreatedBy() {
    return createBy;
  }

  @Override
  public Instant getCreatedDate() {
    return createDate;
  }

  @Override
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  @Override
  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  @Override
  public void setCreatedBy(String createdBy) {
    this.createBy = createdBy;
  }

  @Override
  public void setCreatedDate(Instant createdDate) {
    this.createDate = createdDate;
  }

  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  @Override
  public void setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  @Override
  public Integer getVersion() {
    return version;
  }

  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }
}
