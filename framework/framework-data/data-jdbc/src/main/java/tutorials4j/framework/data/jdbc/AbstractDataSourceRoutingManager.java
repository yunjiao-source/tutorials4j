package tutorials4j.framework.data.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;
import tutorials4j.framework.data.core.exception.DataSourceNameNotFoundException;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public abstract class AbstractDataSourceRoutingManager implements DataSourceRoutingManager {
    private DataSource defaultDataSource;

    private final Map<String, JdbcOptions> jdbcOptionsMap = new HashMap<>();


    @Override
    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(DataSource defaultDataSource) {
        Assert.notNull(defaultDataSource, "defaultDataSource must not be null");
        this.defaultDataSource = defaultDataSource;
    }

    public Map<String, JdbcOptions> getJdbcOptionsMap() {
        return Collections.unmodifiableMap(jdbcOptionsMap);
    }

    @Override
    public void addRoutingJdbcOptions(String name, JdbcOptions jdbcOptions) {
        Assert.notNull(name, "name must not be null");
        Assert.notNull(jdbcOptions, "jdbcOptions must not be null");

        jdbcOptionsMap.put(name, jdbcOptions);
    }

    public void init(DataSource dataSource, Map<String, JdbcOptions> jdbcOptionsMap) {
        Assert.notNull(dataSource, "dataSource must not be null");
        Assert.notNull(jdbcOptionsMap, "jdbcOptionsMap must not be null");

        this.defaultDataSource =dataSource;
        this.jdbcOptionsMap.putAll(jdbcOptionsMap);
    }

    protected DataSource createDataSource(String name) {
        JdbcOptions jdbcOptions = jdbcOptionsMap.get(name);
        if (jdbcOptions == null) {
            throw new DataSourceNameNotFoundException(name);
        }
        log.debug("Tutorials4j - Data |- 创建指定数据源：name = {}, url = {}", name, jdbcOptions.getUrl());
        return createDataSource(name, jdbcOptions);
    }

    protected abstract DataSource createDataSource(String tenant, JdbcOptions jdbcOptions);
}
