package tutorials4j.framework.data.hibernate.tenant;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.common.lang.DefaultConsts;
import tutorials4j.framework.data.core.FrameworkDataException;
import tutorials4j.framework.data.core.tenant.TenantProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 多租户数据源提供者
 *
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractMultiTenantConnectionProvider<T extends DataSource> extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>
        implements HibernatePropertiesCustomizer {
    protected Map<String, T> dataSources = new ConcurrentHashMap<>();
    protected T defaultDataSource;
    protected Map<String, TenantProperties.DataSourceProperties> dataSourcePropertiesMap = new HashMap<>();

    protected abstract T createDataSource(String tenant, TenantProperties.DataSourceProperties properties);

    protected T createDataSource(String tenant) {
        TenantProperties.DataSourceProperties dataSourceProperties = dataSourcePropertiesMap.get(tenant);
        if (dataSourceProperties == null) {
            throw new FrameworkDataException("未配置租户数据源：" + tenant);
        }
        log.debug("Tutorials4j |- 创建租户数据源：[{},{}]", tenant, dataSourceProperties.getUrl());
        return createDataSource(tenant, dataSourceProperties);
    }

    public void init(DataSource dataSource, TenantProperties properties) {
        dataSources.clear();

        // 将key转换成大写
        dataSourcePropertiesMap = properties.getDatasource().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
        this.defaultDataSource = (T) dataSource;
        dataSources.put(DefaultConsts.DEFAULT_TENTANT_CODE, this.defaultDataSource);
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return defaultDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSources.computeIfAbsent(tenantIdentifier, this::createDataSource);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
