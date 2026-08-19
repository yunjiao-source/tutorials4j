package tutorials4j.framework.tenant.hibernate;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.TenantId;

/**
 * 租户实体基类：通过 {@link TenantId} 注解标记租户字段，共享表模式下由 Hibernate 自动填充当前租户标识。
 *
 * @author Yun Jiao
 */
@MappedSuperclass
public class TenantEntity {

  /** 租户标识字段，由 Hibernate 根据当前租户上下文自动填充 */
  @TenantId private String tenantId;

  /**
   * 获取租户编码。
   *
   * @return 租户标识
   */
  public String getTenantCode() {
    return tenantId;
  }

  /**
   * 设置租户编码。
   *
   * @param tenantId 租户标识
   */
  public void setTenantCode(String tenantId) {
    this.tenantId = tenantId;
  }
}
