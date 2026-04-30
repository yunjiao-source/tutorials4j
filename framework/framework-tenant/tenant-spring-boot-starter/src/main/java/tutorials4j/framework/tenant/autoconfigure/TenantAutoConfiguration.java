package tutorials4j.framework.tenant.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.tenant.cache.autoconfigure.TenantCacheConfiguration;
import tutorials4j.framework.tenant.core.autoconfigure.TenantCoreConfiguration;
import tutorials4j.framework.tenant.hibernate.autoconfigure.TenantHibernateConfiguration;

/**
 * 租户（tenant）模块自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({TenantCoreConfiguration.class
        , TenantCacheConfiguration.class
        , TenantHibernateConfiguration.class})
public class TenantAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Tenant |- Tenant Auto Configuration");
    }

}
