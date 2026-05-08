package tutorials4j.framework.data.jdbc;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.support.BeanCreator;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;
import tutorials4j.framework.data.core.exception.DataSourceNotSupportException;

import javax.sql.DataSource;


/**
 * 数据源路由管理器的创建器，实现 {@link BeanCreator} 接口以提供单例实例。
 * <p>
 * 根据默认数据源的实际类型（HikariCP、DBCP2、Druid）自动选择并实例化对应的
 * {@link DataSourceRoutingManager} 实现类，并执行初始化。
 * </p>
 * <p>
 * 该类采用双重检查锁定的懒加载模式，确保线程安全且仅创建一次实例。
 * </p>
 *
 * @author Yun Jiao
 * @see DataSourceRoutingManager
 * @see HikariMapDataSourceRoutingManager
 * @see Dbcp2MapDataSourceRoutingManager
 * @see DruidMapDataSourceRoutingManager
 */
@RequiredArgsConstructor
public class DataSourceRoutingManagerCreator implements BeanCreator<DataSourceRoutingManager> {
    private final DataSource dataSource;

    private DataSourceRoutingManager instance;

    /**
     * 获取数据源路由管理器实例（懒加载、单例）。
     *
     * @return 数据源路由管理器实例
     */
    @Override
    public DataSourceRoutingManager getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (this) {
            if (instance != null) {
                return instance;
            }

            instance = newInstance();
        }

        return instance;
    }

    /**
     * 创建新的数据源路由管理器实例。
     * <p>
     * 根据默认数据源的类名匹配对应的管理器实现：
     * <ul>
     *   <li>com.zaxxer.hikari.HikariDataSource → {@link HikariMapDataSourceRoutingManager}</li>
     *   <li>org.apache.commons.dbcp2.BasicDataSource → {@link Dbcp2MapDataSourceRoutingManager}</li>
     *   <li>com.alibaba.druid.pool.DruidDataSource 或 com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceWrapper → {@link DruidMapDataSourceRoutingManager}</li>
     * </ul>
     * </p>
     *
     * @return 新创建的路由管理器实例
     * @throws DataSourceNotSupportException 如果数据源类型不受支持
     */
    @Override
    public DataSourceRoutingManager newInstance() {
        switch (dataSource.getClass().getName()) {
            case DefaultConsts.CLASS_HIKARI_DATA_SOURCE -> {
                HikariMapDataSourceRoutingManager dataSourceRoutingManager = new HikariMapDataSourceRoutingManager();

                dataSourceRoutingManager.init(dataSource);
                return dataSourceRoutingManager;
            }
            case DefaultConsts.CLASS_DBCP2_BASIC_DATA_SOURCE -> {
                Dbcp2MapDataSourceRoutingManager dataSourceRoutingManager = new Dbcp2MapDataSourceRoutingManager();

                dataSourceRoutingManager.init(dataSource);
                return dataSourceRoutingManager;
            }
            case DefaultConsts.CLASS_DRUID_DATA_SOURCE
                    , DefaultConsts.CLASS_DRUID_DATA_SOURCE_WRAPPER -> {
                DruidMapDataSourceRoutingManager dataSourceRoutingManager = new DruidMapDataSourceRoutingManager();

                dataSourceRoutingManager.init(dataSource);
                return dataSourceRoutingManager;
            }
            default -> throw new DataSourceNotSupportException(dataSource.getClass().getName());
        }

    }

    @Override
    public Class<DataSourceRoutingManager> getCacheManagerClass() {
        return DataSourceRoutingManager.class;
    }

    public DataSource getDefaultDataSource() {
        return this.dataSource;
    }

    @PreDestroy
    public void shutdown() {
        if (instance != null) {
            instance.shutdown();
        }
    }
}
