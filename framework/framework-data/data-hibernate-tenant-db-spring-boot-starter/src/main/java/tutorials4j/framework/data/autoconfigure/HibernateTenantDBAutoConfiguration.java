package tutorials4j.framework.data.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.tenant.TenantConfiguration;
import tutorials4j.framework.data.hibernate.tenant.HibernateDBTenantConfiguration;

/**
 * 租户自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({TenantConfiguration.class, HibernateDBTenantConfiguration.class})
public class HibernateTenantDBAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Hibernate Tenant DB Auto Configuration");
    }

}
