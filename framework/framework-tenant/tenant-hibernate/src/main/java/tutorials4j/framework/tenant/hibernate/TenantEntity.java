package tutorials4j.framework.tenant.hibernate;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.TenantId;

/**
 * 租户实体
 *
 * @author Yun Jiao
 */
@MappedSuperclass
public class TenantEntity {
    @TenantId
    private String tenantId;

    public String getTenantCode() {
        return tenantId;
    }

    public void setTenantCode(String tenantId) {
        this.tenantId = tenantId;
    }
}
