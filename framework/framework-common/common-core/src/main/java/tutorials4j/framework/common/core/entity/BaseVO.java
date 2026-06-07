package tutorials4j.framework.common.core.entity;

import com.google.common.base.Objects;
import java.time.LocalDateTime;

/**
 * 基础数据传输对象（DTO），实现了标识、版本和审计实体接口。
 *
 * <p>作为所有 DTO 的基类，提供通用的 id、version、创建和修改信息字段，并基于 id 实现了 equals/hashCode。
 *
 * @author Yun Jiao
 */
public class BaseVO implements IdEntity<Long>, VersionEntity, AuditingEntity, StatusEntity {
  private Long id;
  private Integer version;
  private LocalDateTime createDate = LocalDateTime.now();
  private LocalDateTime lastModifiedDate = LocalDateTime.now();
  private String createBy;
  private String lastModifiedBy;

  private DataStatusEnum dataStatus;

  @Override
  public String getCreatedBy() {
    return createBy;
  }

  @Override
  public LocalDateTime getCreatedDate() {
    return createDate;
  }

  @Override
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  @Override
  public LocalDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  @Override
  public void setCreatedBy(String createdBy) {
    this.createBy = createdBy;
  }

  @Override
  public void setCreatedDate(LocalDateTime createdDate) {
    this.createDate = createdDate;
  }

  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  @Override
  public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public Integer getVersion() {
    return version;
  }

  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseVO that = (BaseVO) o;
    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public DataStatusEnum getDataStatus() {
    return dataStatus;
  }

  @Override
  public void setDataStatus(DataStatusEnum dataStatus) {
    this.dataStatus = dataStatus;
  }
}
