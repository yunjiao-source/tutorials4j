package tutorials4j.framework.data.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.tenant.TenantConfiguration;
import tutorials4j.framework.data.hibernate.tenant.HibernateTenantConfiguration;

/**
 * 独立数据库租户自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({TenantConfiguration.class, HibernateTenantConfiguration.class})
public class DataHibernateAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Data Hibernate Auto Configuration");
    }

}
