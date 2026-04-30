package tutorials4j.framework.data.jdbc;

import lombok.RequiredArgsConstructor;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;
import tutorials4j.framework.data.core.exception.DataSourceNotSupportException;

import javax.sql.DataSource;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class DataSourceRoutingManagerCreator implements Supplier<DataSourceRoutingManager> {
    private final DataSource dataSource;

    private DataSourceRoutingManager instance;

    @Override
    public DataSourceRoutingManager get() {
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

    public DataSourceRoutingManager newInstance() {
        switch (dataSource.getClass().getName()) {
            case "com.zaxxer.hikari.HikariDataSource" -> {
                HikariMapDataSourceRoutingManager dataSourceRoutingManager = new HikariMapDataSourceRoutingManager();

                dataSourceRoutingManager.init(dataSource);
                return dataSourceRoutingManager;
            }
            case "org.apache.commons.dbcp2.BasicDataSource" -> {
                Dbcp2MapDataSourceRoutingManager dataSourceRoutingManager = new Dbcp2MapDataSourceRoutingManager();

                dataSourceRoutingManager.init(dataSource);
                return dataSourceRoutingManager;
            }
            case "com.alibaba.druid.pool.DruidDataSource"
                    , "com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceWrapper" -> {
                DruidMapDataSourceRoutingManager dataSourceRoutingManager = new DruidMapDataSourceRoutingManager();

                dataSourceRoutingManager.init(dataSource);
                return dataSourceRoutingManager;
            }
            default -> throw new DataSourceNotSupportException(dataSource.getClass().getName());
        }

    }
}
