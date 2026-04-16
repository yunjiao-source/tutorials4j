package tutorials4j.framework.data.hibernate.tenant;

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
    private String tenantCode;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
}
