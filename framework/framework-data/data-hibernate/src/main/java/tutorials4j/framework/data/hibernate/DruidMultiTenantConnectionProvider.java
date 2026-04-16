package tutorials4j.framework.data.hibernate;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.data.core.FrameworkDataException;
import tutorials4j.framework.data.core.tenant.TenantProperties;

import java.util.Properties;

/**
 * Druid 多租户数据源提供者
 *
 * @author Yun Jiao
 */
@Slf4j
public class DruidMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<DruidDataSource> {
    @Override
    protected DruidDataSource createDataSource(TenantProperties.DataSourceProperties dataSourceProperties) {
        Properties properties = defaultDataSource.getConnectProperties();
        try {
            DruidDataSource newDs = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            newDs.setDriverClassName(dataSourceProperties.getDriverClassName());
            newDs.setUrl(dataSourceProperties.getUrl());
            newDs.setUsername(dataSourceProperties.getUsername());
            newDs.setPassword(dataSourceProperties.getPassword());
            return newDs;
        } catch (Exception e) {
            throw new FrameworkDataException("创建租户数据源异常",e);
        }
    }
}
