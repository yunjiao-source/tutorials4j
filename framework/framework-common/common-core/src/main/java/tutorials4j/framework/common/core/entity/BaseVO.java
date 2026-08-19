package tutorials4j.framework.common.core.entity;

import com.google.common.base.Objects;
import java.time.Instant;
import tutorials4j.framework.common.core.bean.DataStatusEnum;

/**
 * 基础数据传输对象（DTO），实现了标识、版本和审计实体接口。
 *
 * <p>作为所有 DTO 的基类，提供通用的 id、version、创建和修改信息字段，并基于 id 实现了 equals/hashCode。
 *
 * @author Yun Jiao
 */
public class BaseVO implements IdEntity<Long>, VersionEntity, AuditingEntity, StatusEntity {
  /** 主键 */
  private Long id;

  /** 版本号 */
  private Integer version;

  /** 创建时间 */
  private Instant createDate;

  /** 最后修改时间 */
  private Instant lastModifiedDate;

  /** 创建人标识 */
  private String createBy;

  /** 最后修改人标识 */
  private String lastModifiedBy;

  /** 数据状态 */
  private DataStatusEnum dataStatus;

  /** {@inheritDoc} */
  @Override
  public String getCreatedBy() {
    return createBy;
  }

  /** {@inheritDoc} */
  @Override
  public Instant getCreatedDate() {
    return createDate;
  }

  /** {@inheritDoc} */
  @Override
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /** {@inheritDoc} */
  @Override
  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  /** {@inheritDoc} */
  @Override
  public void setCreatedBy(String createdBy) {
    this.createBy = createdBy;
  }

  /** {@inheritDoc} */
  @Override
  public void setCreatedDate(Instant createdDate) {
    this.createDate = createdDate;
  }

  /** {@inheritDoc} */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /** {@inheritDoc} */
  @Override
  public void setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  /** {@inheritDoc} */
  @Override
  public Long getId() {
    return id;
  }

  /** {@inheritDoc} */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /** {@inheritDoc} */
  @Override
  public Integer getVersion() {
    return version;
  }

  /** {@inheritDoc} */
  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }

  /** 基于主键 id 判断两个 DTO 是否相等。 */
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

  /** 基于主键 id 生成哈希码。 */
  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  /** {@inheritDoc} */
  @Override
  public DataStatusEnum getDataStatus() {
    return dataStatus;
  }

  /** {@inheritDoc} */
  @Override
  public void setDataStatus(DataStatusEnum dataStatus) {
    this.dataStatus = dataStatus;
  }
}
