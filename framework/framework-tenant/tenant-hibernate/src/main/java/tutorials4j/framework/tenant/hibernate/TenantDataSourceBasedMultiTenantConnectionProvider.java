package tutorials4j.framework.tenant.hibernate;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;

import javax.sql.DataSource;
import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantDataSourceBasedMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>
        implements HibernatePropertiesCustomizer {
    private final DataSourceRoutingManager manager;

    @Override
    protected DataSource selectAnyDataSource() {
        return manager.getDefaultDataSource();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return manager.determineTargetDataSource(tenantIdentifier);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
