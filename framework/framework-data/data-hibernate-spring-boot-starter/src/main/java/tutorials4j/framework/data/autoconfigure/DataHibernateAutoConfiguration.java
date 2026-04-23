package tutorials4j.framework.data.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.data.core.autoconfigure.DataCoreConfiguration;
import tutorials4j.framework.data.core.autoconfigure.DataTenantConfiguration;
import tutorials4j.framework.data.hibernate.autoconfigure.DataHibernateTenantConfiguration;

/**
 * 独立数据库租户自动配置
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@Import({DataCoreConfiguration.class, DataTenantConfiguration.class, DataHibernateTenantConfiguration.class})
public class DataHibernateAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Data Hibernate Auto Configuration");
    }

}
