package tutorials4j.framework.data.hibernate.tenant;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.data.core.DataFrameworkException;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于数据源的多租户连接提供者的抽象基类。
 * <p>
 * 该类继承自 Hibernate 的 {@link AbstractDataSourceBasedMultiTenantConnectionProviderImpl}，
 * 为不同连接池实现（如 HikariCP、Druid、DBCP2）提供了统一的租户数据源管理模板。
 * 维护一个租户标识符到数据源的并发映射，支持懒加载创建租户数据源。
 * 同时实现了 {@link HibernatePropertiesCustomizer}，自动将自身注册到 Hibernate 配置。
 * </p>
 *
 * @param <T> 具体的连接池数据源类型（如 HikariDataSource、DruidDataSource、BasicDataSource）
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractMultiTenantConnectionProvider<T extends DataSource> extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>
        implements HibernatePropertiesCustomizer {
    /**
     * 租户标识符到数据源的缓存映射，支持并发访问。
     */
    protected Map<String, T> dataSources = new ConcurrentHashMap<>();

    /**
     * 租户标识符到其数据源配置选项的映射。
     */
    protected Map<String, DataTenantProperties.DataSourceOptions> dataSourceOptionsMap = new HashMap<>();

    /**
     * 为指定租户创建新的数据源实例。
     *
     * @param tenant  租户标识符
     * @param options 该租户的数据源配置（URL、用户名、密码等）
     * @return 新建的数据源
     */
    protected abstract T createDataSource(String tenant, DataTenantProperties.DataSourceOptions options);

    /**
     * 获取默认租户的数据源。
     *
     * @return 默认数据源
     */
    protected abstract T getDefaultDataSource();

    /**
     * 设置默认数据源（由外部传入）。
     *
     * @param dataSource 外部提供的默认数据源
     * @throws DataFrameworkException 如果数据源类型与泛型类型不匹配
     */
    protected abstract void setDefaultDataSource(DataSource dataSource);

    /**
     * 为指定租户创建（或获取已有）数据源，线程安全。
     *
     * @param tenant 租户标识符
     * @return 对应的数据源
     * @throws DataFrameworkException 如果未找到该租户的配置
     */
    protected synchronized T createDataSource(String tenant) {
        // 同步方法 + 校验，解决多线程问题
        if (dataSources.containsKey(tenant)) {
            return dataSources.get(tenant);
        }

        DataTenantProperties.DataSourceOptions dataSourceOptions = dataSourceOptionsMap.get(tenant);
        if (dataSourceOptions == null) {
            throw new DataFrameworkException("未配置租户数据源：" + tenant);
        }
        log.debug("Tutorials4j - Data |- 创建租户数据源：[{},{}]", tenant, dataSourceOptions.getUrl());
        return createDataSource(tenant, dataSourceOptions);
    }

    /**
     * 初始化多租户连接提供者。
     * <p>
     * 清空已有缓存，加载所有租户配置，设置默认数据源，
     * 并将默认数据源以 {@link DefaultConsts#DEFAULT_TENTANT_CODE} 为键放入缓存。
     * </p>
     *
     * @param dataSource 默认数据源（通常是主数据源）
     * @param properties 包含所有租户数据源配置的属性对象
     */
    public void init(DataSource dataSource, DataTenantProperties properties) {
        dataSources.clear();

        // 将key转换成大写
        dataSourceOptionsMap = properties.getDatasource().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
        setDefaultDataSource(dataSource);
        dataSources.put(DefaultConsts.DEFAULT_TENTANT_CODE, this.getDefaultDataSource());
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return getDefaultDataSource();
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
